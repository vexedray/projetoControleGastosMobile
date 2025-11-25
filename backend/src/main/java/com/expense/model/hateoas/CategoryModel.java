package com.expense.model.hateoas;

import org.springframework.hateoas.RepresentationModel;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryModel extends RepresentationModel<CategoryModel> {
    
    private Long id;
    private String name;
    private String description;
    private String color;
    private String icon;
    
    public CategoryModel() {
    }
    
    public CategoryModel(Long id, String name, String description, String color, String icon) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.color = color;
        this.icon = icon;
    }
    
    @JsonProperty("id")
    public Long getCategoryId() {
        return id;
    }
    
    public void setCategoryId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
}
