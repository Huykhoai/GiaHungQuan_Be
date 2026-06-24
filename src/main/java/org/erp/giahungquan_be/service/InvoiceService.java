package org.erp.giahungquan_be.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.erp.giahungquan_be.dto.InvoiceDto;
import org.erp.giahungquan_be.dto.InvoiceItemDto;
import org.erp.giahungquan_be.entity.DiningTable;
import org.erp.giahungquan_be.entity.Invoice;
import org.erp.giahungquan_be.entity.InvoiceItem;
import org.erp.giahungquan_be.entity.MenuItem;
import org.erp.giahungquan_be.mapper.InvoiceItemMapper;
import org.erp.giahungquan_be.mapper.InvoiceMapper;
import org.erp.giahungquan_be.repository.DiningTableRepository;
import org.erp.giahungquan_be.repository.InvoiceItemRepository;
import org.erp.giahungquan_be.repository.InvoiceRepository;
import org.erp.giahungquan_be.repository.MenuItemRepository;
import org.erp.giahungquan_be.request.CreateInvoiceRequest;
import org.erp.giahungquan_be.request.OrderItemRequest;
import org.erp.giahungquan_be.request.OrderItemsRequest;
import org.erp.giahungquan_be.response.Message;
import org.erp.giahungquan_be.util.BatchFetch;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.messaging.simp.SimpMessagingTemplate;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InvoiceService {

    InvoiceRepository invoiceRepository;
    InvoiceItemRepository invoiceItemRepository;
    DiningTableRepository tableRepository;
    MenuItemRepository menuItemRepository;
    SimpMessagingTemplate messagingTemplate;
    InvoiceMapper invoiceMapper;
    InvoiceItemMapper invoiceItemMapper;

    public List<InvoiceDto> getActiveInvoices() {
        LocalDateTime startDay = LocalDate.now().atStartOfDay();
        List<Invoice> invoices = invoiceRepository.findAllByStatusAndCreatedAtGreaterThanEqualOrderByIdDesc("EATING",
                startDay);
        return invoiceMapper.toDtoList(invoices);
    }

    public List<InvoiceDto> getInvoiceHistory(LocalDate date, String tableName) {
        LocalDateTime startDate = LocalDateTime.of(1970, 1, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2100, 1, 1, 0, 0);

        if (date != null) {
            startDate = date.atStartOfDay();
            endDate = date.plusDays(1).atStartOfDay();
        } else if (tableName == null || tableName.trim().isEmpty()) {
            startDate = LocalDate.now().atStartOfDay();
            endDate = LocalDate.now().plusDays(1).atStartOfDay();
        }

        String searchTable = (tableName == null) ? "" : tableName;

        List<Invoice> invoices = invoiceRepository.findInvoicesWithFilters("PAID", startDate, endDate, searchTable);
        return invoiceMapper.toDtoList(invoices);
    }

    @Transactional
    public InvoiceDto createInvoice(CreateInvoiceRequest request) {
        if (invoiceRepository.findByTableIdAndStatus(request.getTableId(), "EATING").isPresent()) {
            throw new RuntimeException("Bàn này đang có khách, không thể mở phiên mới!");
        }

        DiningTable table = tableRepository.findByIdAndDeletedAtIsNull(request.getTableId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn"));

        table.setStatus("OCCUPIED");
        tableRepository.save(table);

        Invoice invoice = Invoice.builder()
                .table(table)
                .tableName(table.getName())
                .status("EATING")
                .totalAmount(BigDecimal.ZERO)
                .build();
        return invoiceMapper.toDto(invoiceRepository.save(invoice));
    }

    public InvoiceDto getInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        return invoiceMapper.toDto(invoice);
    }

    public List<InvoiceItemDto> getInvoiceItems(Long invoiceId, String status) {
        List<InvoiceItem> items = new ArrayList<>();
        if (status != null && !status.isEmpty()) {
            items.addAll(invoiceItemRepository.findAllByInvoiceIdAndStatusOrderByIdAsc(invoiceId, status));
        } else {
            items.addAll(invoiceItemRepository.findAllByInvoiceIdOrderByIdAsc(invoiceId));
        }
        return invoiceItemMapper.toDtoList(items);
    }

    public List<InvoiceItemDto> getAllPendingItems() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        List<InvoiceItem> items = invoiceItemRepository
                .findAllByStatusAndCreatedAtGreaterThanEqualOrderByCreatedAtAsc("PENDING", startOfDay);
        return invoiceItemMapper.toDtoList(items);
    }

    @Transactional
    public void orderItems(Long invoiceId, OrderItemsRequest request) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn"));

        if (!"EATING".equals(invoice.getStatus())) {
            throw new RuntimeException("Hóa đơn đã thanh toán hoặc hủy, không thể gọi thêm món!");
        }
        List<Long> menuItemIds = request.getItems().stream()
                .map(OrderItemRequest::getMenuItemId)
                .collect(Collectors.toList());

        Map<Long, MenuItem> menuItemMap = BatchFetch.fetch(
                menuItemIds,
                menuItemRepository::findAllByIdInAndDeletedAtIsNull,
                MenuItem::getId,
                BatchFetch.BATCH_FETCH_SIZE);

        for (OrderItemRequest itemReq : request.getItems()) {
            MenuItem menuItem = menuItemMap.get(itemReq.getMenuItemId());

            if (menuItem == null) {
                throw new IllegalArgumentException("Món ăn không tồn tại");
            }

            InvoiceItem invoiceItem = InvoiceItem.builder()
                    .invoice(invoice)
                    .menuItem(menuItem)
                    .name(menuItem.getName())
                    .price(menuItem.getPrice())
                    .quantity(itemReq.getQuantity())
                    .note(itemReq.getNote())
                    .status("PENDING")
                    .tableName(invoice.getTableName())
                    .build();

            invoiceItemRepository.save(invoiceItem);
        }

        messagingTemplate.convertAndSend("/topic/kitchen", "NEW_ORDER");
        messagingTemplate.convertAndSend("/topic/invoice/" + invoice.getId(), "UPDATED");

    }

    @Transactional
    public InvoiceItem markItemAsServed(Long itemId) {
        InvoiceItem item = invoiceItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn trong hóa đơn"));

        if ("SERVED".equals(item.getStatus())) {
            throw new RuntimeException("Món này đã được phục vụ rồi!");
        }

        item.setStatus("SERVED");
        invoiceItemRepository.save(item);

        Invoice invoice = item.getInvoice();

        BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
        BigDecimal totalItem = item.getPrice().multiply(quantity);
        invoice.setTotalAmount(invoice.getTotalAmount().add(totalItem));
        invoiceRepository.save(invoice);

        messagingTemplate.convertAndSend("/topic/kitchen", "ITEM_SERVED");
        messagingTemplate.convertAndSend("/topic/invoice/" + invoice.getId(), "UPDATED");

        return item;
    }

    @Transactional
    public void payInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy hóa đơn"));

        if (invoice.getStatus().equals("PAID")) {
            throw new IllegalArgumentException("Hóa đơn đã được thanh toán");
        }

        List<InvoiceItem> pendingItems = invoiceItemRepository.findAllByInvoiceIdAndStatusOrderByIdAsc(invoiceId,
                "PENDING");
        if (!pendingItems.isEmpty()) {
            throw new RuntimeException("Còn món chưa ra đồ, không thể thanh toán!");
        }

        invoice.setStatus("PAID");
        invoiceRepository.save(invoice);

        DiningTable table = tableRepository.findById(invoice.getTable().getId()).orElse(null);
        if (table != null) {
            table.setStatus("EMPTY");
            tableRepository.save(table);
        }
    }

    @Transactional
    public void cancelPendingItem(Long itemId) {
        InvoiceItem item = invoiceItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy món ăn trong hóa đơn"));

        if (!"PENDING".equals(item.getStatus())) {
            throw new RuntimeException("Chỉ có thể hủy những món đang chờ phục vụ (PENDING)!");
        }

        Long invoiceId = item.getInvoice().getId();
        invoiceItemRepository.delete(item);

        // Phát tín hiệu cập nhật cho các màn hình (Bếp và Màn Gọi Món)
        messagingTemplate.convertAndSend("/topic/kitchen", "ITEM_CANCELLED");
        messagingTemplate.convertAndSend("/topic/invoice/" + invoiceId, "UPDATED");
    }

    @Transactional
    public Message deleteEmptyInvoice(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        boolean existsItems = invoiceItemRepository.existsInvoiceItemByInvoice_Id(invoiceId);

        if (existsItems) {
            return new Message(HttpStatus.BAD_REQUEST.value(), "Hóa đơn đã lên món, không thể xóa");
        }
        DiningTable table = tableRepository.findById(invoice.getTable().getId()).orElse(null);
        if (table != null) {
            table.setStatus("EMPTY");
            tableRepository.save(table);
        }

        invoiceRepository.delete(invoice);
        return new Message(HttpStatus.OK.value(), "Hóa đơn đã được xóa");
    }
}
