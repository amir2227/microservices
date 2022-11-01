package com.amircode.inventoryservice.service;

import com.amircode.inventoryservice.dto.InventoryResponse;
import com.amircode.inventoryservice.model.Inventory;
import com.amircode.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public List<InventoryResponse> isInStock(List<String> skuCode){
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(inventory ->
                    InventoryResponse.builder()
                            .isInStock(inventory.getQuantity() > 0)
                            .qty(inventory.getQuantity())
                            .skuCode(inventory.getSkuCode())
                            .build()
                ).toList();

    }
}
