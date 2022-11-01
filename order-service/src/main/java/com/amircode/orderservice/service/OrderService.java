package com.amircode.orderservice.service;

import com.amircode.orderservice.dto.InventoryResponse;
import com.amircode.orderservice.dto.OrderLineItemsDto;
import com.amircode.orderservice.dto.OrderRequest;
import com.amircode.orderservice.event.OrderPlacedEvent;
import com.amircode.orderservice.model.Order;
import com.amircode.orderservice.model.OrderLineItems;
import com.amircode.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final WebClient.Builder webClientBuilder;
    private final KafkaTemplate<String,Object> kafkaTemplate;
    public Order placeOrder(OrderRequest orderRequest){
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        List<OrderLineItems> orderLineItems = orderRequest.getOrderLineItemsDto().stream().map(this::mapToEntity).toList();
        order.setOderLineItems(orderLineItems);
        List<String> skuCodes = order.getOderLineItems().stream().map(OrderLineItems::getSkucode).toList();
        InventoryResponse[] inventoryResponses = webClientBuilder.build().get()
                        .uri("http://inventory-service/api/inventory",
                                uriBuilder -> uriBuilder.queryParam("skuCode",skuCodes).build())
                                .retrieve()
                                        .bodyToMono(InventoryResponse[].class)
                                                .block(); // block make the request async
        boolean allProductInStock = Arrays.stream(inventoryResponses).allMatch(InventoryResponse::isInStock);
        if(!allProductInStock) throw new RuntimeException("product is not in stock");
        orderRepository.saveAndFlush(order);
        kafkaTemplate.send("notificationTopic",new OrderPlacedEvent(order.getOrderNumber()));
        return order;
    }

    public OrderLineItems mapToEntity(OrderLineItemsDto dto){
        OrderLineItems orderLineItems = new OrderLineItems();
        orderLineItems.setQuantity(dto.getQuantity());
        orderLineItems.setPrice(dto.getPrice());
        orderLineItems.setSkucode(dto.getSkucode());
        return  orderLineItems;
    }
}
