package org.efrenjm.investingtracker.domain.model.wallet.exceptions;

import org.efrenjm.investingtracker.domain.exception.ResourceNotFoundException;

public class CategoryNotFound extends ResourceNotFoundException {
    public CategoryNotFound(String name) {
        super("Subcategory doesn't exist: " + name);
    }
}
