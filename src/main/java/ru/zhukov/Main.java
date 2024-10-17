package ru.zhukov;

import ru.zhukov.models.Customer;
import ru.zhukov.models.Order;
import ru.zhukov.models.Product;
import ru.zhukov.utils.CustomersInitializer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Calendar.FEBRUARY;

public class Main {
    public static void main(String[] args) {

        List<Customer> customers = CustomersInitializer.getCustomers();

        // Task 1 - Получите список продуктов из категории "Books" с ценой более 100
        List<Product> booksWithPriceBelow100 = customers.stream()
                .flatMap(c -> c.getOrders().stream())
                .flatMap(order -> order.getProducts().stream())
                .distinct()
                .filter(p -> "Books".equals(p.getCategory()))
                .filter(p -> p.getPrice().compareTo(BigDecimal.valueOf(100)) > 0)
                .toList();
        System.out.println(booksWithPriceBelow100);

        // Task 2 - Получите список заказов с продуктами из категории "Children's products"
        List<Order> ordersWithChildrenProducts = customers.stream()
                .flatMap(customer -> customer.getOrders().stream())
                .filter(order -> order.getProducts().stream().anyMatch(p -> p.getCategory().equals("Children's products")))
                .toList();
        System.out.println(ordersWithChildrenProducts);

        // Task 3 - Получите список продуктов из категории "Toys" и примените скидку 10% и получите сумму всех продуктов
        Optional<BigDecimal> toysProductsSumWith10PercentDiscount = customers.stream()
                .flatMap(c -> c.getOrders().stream())
                .flatMap(order -> order.getProducts().stream())
                .distinct()
                .filter(p -> p.getCategory().equals("Toys"))
                .map(p -> p.getPrice().multiply(new BigDecimal("1.1")))
                .reduce(BigDecimal::add);
        toysProductsSumWith10PercentDiscount.ifPresentOrElse(System.out::println, () -> System.out.println("Не удалось рассчитать сумму игрушек со скидкой"));


        // Task 4 - Получите список продуктов, заказанных клиентом второго уровня между 01-фев-2021 и 01-апр-2021
        List<Product> productsOrderedBy2lvlCustomerBetweenDates = customers.stream()
                .filter(customer -> customer.getLevel().equals(2L))
                .limit(1)
                .flatMap(customer -> customer.getOrders().stream())
                .filter(order -> order.getOrderDate().isAfter(LocalDate.of(2021, Month.FEBRUARY, 1)))
                .filter(order -> order.getOrderDate().isBefore(LocalDate.of(2021, Month.APRIL, 1)))
                .flatMap(order -> order.getProducts().stream())
                .distinct()
                .collect(Collectors.toList());
        System.out.println(productsOrderedBy2lvlCustomerBetweenDates);

        // Task 5 - Получите топ 2 самые дешевые продукты из категории "Books".
        List<Product> top2CheapestBooks = customers.stream()
                .flatMap(customer -> customer.getOrders().stream())
                .flatMap(order -> order.getProducts().stream())
                .distinct()
                .filter(product -> product.getCategory().equals("Books"))
                .sorted(Comparator.comparing(Product::getPrice))
                .limit(2)
                .toList();
        System.out.println(top2CheapestBooks);

        // Task 6 - Получите 3 самых последних сделанных заказа.
        List<Order> mostRecent3Orders = customers.stream()
                .flatMap(customer -> customer.getOrders().stream())
                .sorted(Comparator.comparing(Order::getDeliveryDate).reversed())
                .limit(3)
                .toList();
        System.out.println(mostRecent3Orders);

        // Task 7 - Получите список заказов, сделанных 15-марта-2021, выведите id заказов в консоль и затем верните
        //список их продуктов.
        List<Product> productsInOrdersOn15thOfMay21 = customers.stream()
                .flatMap(customer -> customer.getOrders().stream())
                .filter(order -> order.getOrderDate().equals(LocalDate.of(2021, Month.MARCH, 15)))
                .peek(order -> System.out.println(order.getId()))
                .flatMap(order -> order.getProducts().stream())
                .distinct()
                .toList();
        System.out.println(productsInOrdersOn15thOfMay21);

        // Task 8 - Рассчитайте общую сумму всех заказов, сделанных в феврале 2021
        Optional<BigDecimal> march21OrdersSum = customers.stream()
                .flatMap(customer -> customer.getOrders().stream())
                .filter(order -> order.getOrderDate().getYear() == 2021 & order.getOrderDate().getMonth() == Month.FEBRUARY)
                .flatMap(order -> order.getProducts().stream())
                .map(Product::getPrice)
                .reduce(BigDecimal::add);
        march21OrdersSum.ifPresentOrElse(System.out::println, () -> System.out.println("Не удалось рассчитать сумму заказов"));

        // Task 9 - Рассчитайте средний платеж по заказам, сделанным 14-марта-2021
        Double averageOrderPaymentOn14thOfMarch21 = customers.stream()
                .flatMap(customer -> customer.getOrders().stream())
                .filter(order -> order.getOrderDate().equals(LocalDate.of(2021, Month.MARCH, 14)))
                .map(order -> order.getProducts().stream()
                        .map(Product::getPrice)
                        .reduce(BigDecimal::add).orElse(BigDecimal.ZERO))
                .collect(Collectors.averagingDouble(BigDecimal::doubleValue)); // Рассчитываем используя Double, т.к. для BigDecimal пришлось бы писать кучу вспомогательного кода
        System.out.println(averageOrderPaymentOn14thOfMarch21);

        // Task 10 - Получите набор статистических данных (сумма, среднее, максимум, минимум, количество) для всех
        //продуктов категории "Книги".
        Map<String, BigDecimal> booksStatistics = new HashMap<>();
        List<BigDecimal> booksPrices = customers.stream()
                .flatMap(customer -> customer.getOrders().stream())
                .flatMap(order -> order.getProducts().stream())
                .distinct()
                .filter(product -> product.getCategory().equals("Books"))
                .map(Product::getPrice)
                .toList();
        booksStatistics.put("sum", booksPrices.stream().reduce(BigDecimal::add).orElse(BigDecimal.ZERO));
        booksStatistics.put("count", BigDecimal.valueOf(booksPrices.size()));
        // booksStatistics.put("average", booksPrices.stream().reduce((a, b) -> a.add(b).divide(booksStatistics.get("count"), RoundingMode.CEILING)).orElse(BigDecimal.ZERO));
        booksStatistics.put("average", booksStatistics.get("sum").divide(booksStatistics.get("count"), RoundingMode.UP));
        booksStatistics.put("min", booksPrices.stream().min(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
        booksStatistics.put("max", booksPrices.stream().max(BigDecimal::compareTo).orElse(BigDecimal.ZERO));
        System.out.println(booksStatistics);

        // Task 11 - Получите данные Map<Long, Integer> → key - id заказа, value - кол-во товаров в заказе
        Map<Long, Integer> ordersProductsCount = customers.stream()
                .flatMap(customer -> customer.getOrders().stream())
                .collect(Collectors.toMap(Order::getId, order -> order.getProducts().size()));
        System.out.println(ordersProductsCount);

        // Task 12 -  Создайте Map<Customer, List<Order>> → key - покупатель, value - список его заказов
        Map<Customer, List<Order>> customersOrders = customers.stream()
                .collect(Collectors.toMap(customer -> customer, customer -> customer.getOrders().stream().toList()));
        System.out.println(customersOrders);

        // Task 13 - Создайте Map<Order, Double> → key - заказ, value - общая сумма продуктов заказа.
        Map<Order, BigDecimal> ordersSum = customers.stream()
                .flatMap(customer -> customer.getOrders().stream())
                .collect(Collectors.toMap(order -> order, order -> order.getProducts().stream()
                        .map(Product::getPrice)
                        .reduce(BigDecimal::add).orElse(BigDecimal.ZERO)));
        System.out.println(ordersSum);

        // Task 14 - Получите Map<String, List<String>> → key - категория, value - список названий товаров в категории
        Map<String, List<String>> categoryProducts = customers.stream()
                .flatMap(customer -> customer.getOrders().stream())
                .flatMap(order -> order.getProducts().stream())
                .distinct()
                .collect(Collectors.groupingBy(
                        Product::getCategory,
                        Collectors.mapping(Product::getName, Collectors.toList())
                ));
        System.out.println(categoryProducts);

        // Task 15 - Получите Map<String, Product> → самый дорогой продукт по каждой категории.
        Map<String, Product> mostExpensiveProductByCategory = customers.stream()
                .flatMap(customer -> customer.getOrders().stream())
                .flatMap(order -> order.getProducts().stream())
                .distinct()
                .collect(Collectors.groupingBy(Product::getCategory,
                        Collectors.collectingAndThen(
                                Collectors.maxBy(Comparator.comparing(Product::getPrice)),
                                productOptional -> productOptional.orElse(null))
                        ));
        System.out.println(mostExpensiveProductByCategory);
    }
}