package com.gsports.java.oop;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class Admin extends User{
    private static int adminCount = 0;
    private String accessLevel;

    public Admin() {}

    public Admin(String userID, String username, String email, String password, String accessLevel) {
        super(username, email, password);
        this.setUserID(generateAdminID()); 
        this.accessLevel = accessLevel;
    }

    public static int getAdminCount() {
        return Admin.adminCount;
    }

    public static void setAdminCount (int adminCount) {
        Admin.adminCount = adminCount;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    private String generateAdminID() {
        return "A" + String.format("%03d", ++Admin.adminCount);
    }
    
    public String generateSalesReport(List<Order> orders, List<Product> products, 
                                LocalDateTime startDate, LocalDateTime endDate) {
        StringBuilder report = new StringBuilder();
        
        // 1. Report Header
        report.append("\n┌───────────────────────────────────────────────────────────┐\n");
        report.append("│                GSPORTS SALES REPORT                       │\n");
        report.append("├───────────────────────────────────────────────────────────┤\n");
            
        // Format dates for display
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String reportDate = LocalDateTime.now().format(formatter);
        String periodStart = startDate != null ? startDate.format(formatter) : "All time";
        String periodEnd = endDate != null ? endDate.format(formatter) : "Present";
        
        report.append(String.format("│ Generated by: %-43s │\n", this.getUsername()));
        report.append(String.format("│ Report Date: %-44s │\n", reportDate));
        report.append(String.format("│ Period: %-49s │\n", periodStart + " to " + periodEnd));
        report.append("└───────────────────────────────────────────────────────────┘\n\n");
        
        // Filter orders by date range if specified
        List<Order> filteredOrders = orders.stream()
                .filter(order -> (startDate == null || !order.getOrderDate().isBefore(startDate)))
                .filter(order -> (endDate == null || !order.getOrderDate().isAfter(endDate)))
                .filter(order -> order.getStatus() != Order.OrderStatus.CANCELLED)
                .collect(Collectors.toList());
        
        // 2. Summary Statistics
        double totalRevenue = filteredOrders.stream()
                .mapToDouble(Order::getTotalAmount)
                .sum();
        
        long totalOrders = filteredOrders.size();
        long completedOrders = filteredOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.COMPLETED)
                .count();
        long pendingOrders = filteredOrders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.PENDING || 
                            o.getStatus() == Order.OrderStatus.PROCESSING || 
                            o.getStatus() == Order.OrderStatus.SHIPPED || 
                            o.getStatus() == Order.OrderStatus.DELIVERED)
                .count();
        
        report.append("┌───────────────────────────────────────────────────────────┐\n");
        report.append("│                         SUMMARY                           │\n");
        report.append("├───────────────────────────────────────────────────────────┤\n");
        report.append(String.format("│ Total Revenue: $%-41.2f │\n", totalRevenue));
        report.append(String.format("│ Total Orders: %-43d │\n", totalOrders));
        report.append(String.format("│ Completed Orders: %-39d │\n", completedOrders));
        report.append(String.format("│ Pending/Processing Orders: %-30d │\n", pendingOrders));
        report.append("├───────────────────────────────────────────────────────────┤\n");
        
        // 3. Revenue by Product Category
        report.append(String.format("│ REVENUE BY PRODUCT CATEGORY %-30s│\n", ""));
        report.append("├───────────────────────────────────────────────────────────┤\n");
        report.append(String.format("│ %-15s: $%-10s (%6s) - %3s         │\n", "Category", "Revenue", "Percentage", "Units"));
        report.append("├───────────────────────────────────────────────────────────┤\n");
        
        // Create map to hold category totals
        Map<String, Double> categoryRevenue = new HashMap<>();
        Map<String, Integer> categorySales = new HashMap<>();
        
        // Process all order items to calculate per-category totals
        for (Order order : filteredOrders) {
            for (CartItem item : order.getItems()) {
                String category = item.getProduct().getProductType();
                double itemRevenue = item.getSubtotal();
                int quantity = item.getQuantity();
                
                categoryRevenue.put(category, categoryRevenue.getOrDefault(category, 0.0) + itemRevenue);
                categorySales.put(category, categorySales.getOrDefault(category, 0) + quantity);
            }
        }
        
        // Sort categories by revenue (descending)
        List<String> sortedCategories = new ArrayList<>(categoryRevenue.keySet());
        sortedCategories.sort((c1, c2) -> 
                Double.compare(categoryRevenue.get(c2), categoryRevenue.get(c1)));
        
        // Display category data
        for (String category : sortedCategories) {
            double revenue = categoryRevenue.get(category);
            int units = categorySales.get(category);
            double percentage = (revenue / totalRevenue) * 100;
            
            report.append(String.format("│ %-15s: $%-10.2f (%6.2f%%) - %9d        │\n", 
                    category.length() > 15 ? category.substring(0, 12) + "..." : category, 
                    revenue, percentage, units));
        }
        report.append("├───────────────────────────────────────────────────────────┤\n");
        
        // 4. Top 5 Best Selling Products
        report.append(String.format("│ TOP 5 BEST SELLING PRODUCTS %-29s │\n", ""));
        report.append("├───────────────────────────────────────────────────────────┤\n");
        report.append(String.format("│ %-30s: %3s units - $%-10s │\n", "Product Name", "Units", "Revenue"));
        report.append("├───────────────────────────────────────────────────────────┤\n");
        
        Map<String, Integer> productSales = new HashMap<>();
        Map<String, Double> productRevenue = new HashMap<>();
        Map<String, String> productNames = new HashMap<>();
        
        // Collect product sales data
        for (Order order : filteredOrders) {
            for (CartItem item : order.getItems()) {
                String productId = item.getProduct().getProdID();
                String productName = item.getProduct().getProdName();
                int quantity = item.getQuantity();
                double revenue = item.getSubtotal();
                
                productSales.put(productId, productSales.getOrDefault(productId, 0) + quantity);
                productRevenue.put(productId, productRevenue.getOrDefault(productId, 0.0) + revenue);
                productNames.put(productId, productName);
            }
        }
        
        // Get top 5 by sales quantity
        List<String> productIds = new ArrayList<>(productSales.keySet());
        productIds.sort((p1, p2) -> Integer.compare(productSales.get(p2), productSales.get(p1)));
        
        int count = 0;
        for (String productId : productIds) {
            if (count >= 5) break;
            
            String name = productNames.get(productId);
            int units = productSales.get(productId);
            double revenue = productRevenue.get(productId);
            
            report.append(String.format("│ %-30s: %3d units - $%-13.2f│\n", 
                    name.length() > 30 ? name.substring(0, 27) + "..." : name, 
                    units, revenue));
            count++;
        }
        report.append("└───────────────────────────────────────────────────────────┘\n\n");
        
        // 5. Monthly Trend (if date range spans multiple months)
        if (startDate != null && endDate != null && 
            !YearMonth.from(startDate).equals(YearMonth.from(endDate))) {
            
                report.append("┌───────────────────────────────────────────────────────────┐\n");
                report.append("│                  MONTHLY REVENUE TREND                    │\n");
                report.append("├───────────────────────────────────────────────────────────┤\n");
                report.append(String.format("│ %-10s    %-42s │\n", "Month", "Revenue"));
                report.append("├───────────────────────────────────────────────────────────┤\n");
            
            // Group orders by month
            Map<YearMonth, Double> monthlyRevenue = new TreeMap<>();
            
            for (Order order : filteredOrders) {
                YearMonth orderMonth = YearMonth.from(order.getOrderDate());
                monthlyRevenue.put(orderMonth, 
                        monthlyRevenue.getOrDefault(orderMonth, 0.0) + order.getTotalAmount());
            }
            
            for (Map.Entry<YearMonth, Double> entry : monthlyRevenue.entrySet()) {
                report.append(String.format("│ %-10s    $%-39.2f │\n", 
                        entry.getKey().format(DateTimeFormatter.ofPattern("MMM yyyy")), 
                        entry.getValue()));
        }
            report.append("└───────────────────────────────────────────────────────────┘\n\n");
        }
        
        return report.toString();
    }
}
