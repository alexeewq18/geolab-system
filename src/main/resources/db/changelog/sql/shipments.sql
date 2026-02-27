CREATE TABLE shipments
(
    id             BIGINT PRIMARY KEY,
    order_id       BIGINT NOT NULL REFERENCES lab_orders(id),
    amount         INTEGER NOT NULL,
    shipment_date  DATE NOT NULL,
    note           TEXT,
    shipped_at     DATE NOT NULL DEFAULT CURRENT_DATE,
    created_at     TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_shipments_order   ON shipments(order_id);
CREATE INDEX idx_shipments_date    ON shipments(shipped_at);