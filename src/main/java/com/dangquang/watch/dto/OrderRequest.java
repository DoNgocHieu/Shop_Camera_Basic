package com.dangquang.watch.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class OrderRequest {
    
    @NotBlank(message = "Họ tên không được để trống")
    @Size(max = 100, message = "Họ tên không được quá 100 ký tự")
    private String fullName;
    
    @NotBlank(message = "Số điện thoại không được để trống")
    @Size(max = 15, message = "Số điện thoại không được quá 15 ký tự")
    private String phone;
    
    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Email không được để trống")
    private String email;
    
    @NotBlank(message = "Địa chỉ không được để trống")
    @Size(max = 500, message = "Địa chỉ không được quá 500 ký tự")
    private String address;
    
    @Size(max = 1000, message = "Ghi chú không được quá 1000 ký tự")
    private String notes;
    
    private String paymentMethod;
    
    // Constructors
    public OrderRequest() {}
    
    public OrderRequest(String fullName, String phone, String email, String address) {
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }
    
    // Getters and Setters
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
