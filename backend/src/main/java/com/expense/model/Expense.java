package com.expense.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "gastos")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(nullable = false)
    private LocalDateTime data;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;

    public Expense() {
        this.data = LocalDateTime.now();
    }

    public Expense(BigDecimal valor, LocalDateTime data) {
        this.valor = valor;
        this.data = data;
    }

    public Expense(BigDecimal valor, LocalDateTime data, Category category) {
        this.valor = valor;
        this.data = data;
        this.category = category;
    }
    
    public Expense(BigDecimal valor, Category category) {
        this.valor = valor;
        this.data = LocalDateTime.now();
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}