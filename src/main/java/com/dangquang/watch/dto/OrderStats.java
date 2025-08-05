package com.dangquang.watch.dto;

public class OrderStats {
    private long totalOrders;
    private long pendingOrders;
    private long processingOrders;
    private long shippingOrders;
    private long deliveredOrders;
    private long cancelledOrders;
    
    // Constructors
    public OrderStats() {}
    
    public OrderStats(long totalOrders, long pendingOrders, long processingOrders, 
                     long shippingOrders, long deliveredOrders, long cancelledOrders) {
        this.totalOrders = totalOrders;
        this.pendingOrders = pendingOrders;
        this.processingOrders = processingOrders;
        this.shippingOrders = shippingOrders;
        this.deliveredOrders = deliveredOrders;
        this.cancelledOrders = cancelledOrders;
    }
    
    // Getters and Setters
    public long getTotalOrders() {
        return totalOrders;
    }
    
    public void setTotalOrders(long totalOrders) {
        this.totalOrders = totalOrders;
    }
    
    public long getPendingOrders() {
        return pendingOrders;
    }
    
    public void setPendingOrders(long pendingOrders) {
        this.pendingOrders = pendingOrders;
    }
    
    public long getProcessingOrders() {
        return processingOrders;
    }
    
    public void setProcessingOrders(long processingOrders) {
        this.processingOrders = processingOrders;
    }
    
    public long getShippingOrders() {
        return shippingOrders;
    }
    
    public void setShippingOrders(long shippingOrders) {
        this.shippingOrders = shippingOrders;
    }
    
    public long getDeliveredOrders() {
        return deliveredOrders;
    }
    
    public void setDeliveredOrders(long deliveredOrders) {
        this.deliveredOrders = deliveredOrders;
    }
    
    public long getCancelledOrders() {
        return cancelledOrders;
    }
    
    public void setCancelledOrders(long cancelledOrders) {
        this.cancelledOrders = cancelledOrders;
    }
}
