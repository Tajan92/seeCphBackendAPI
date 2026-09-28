package app.enums;

import lombok.Getter;

@Getter
public enum AddPlacement {
    FRONTPAGE_HEADER(300.00),
    FRONTPAGE_HIGHLIGHT(500.00);

    private final double price;

    AddPlacement(double price) {
        this.price = price;
    }
}
