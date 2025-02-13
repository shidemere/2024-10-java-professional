package homework;

import java.util.*;

public class CustomerService {

    private final List<Map.Entry<Customer, String>> entries = new ArrayList<>();

    public Map.Entry<Customer, String> getSmallest() {
        return entries.stream()
                .min(Comparator.comparingLong(e -> e.getKey().getScores()))
                .map(this::copyEntry)
                .orElse(null);
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        if (entries.isEmpty() || customer == null) {
            return null;
        }

        long targetScore = customer.getScores();

        return entries.stream()
                .filter(e -> e.getKey().getScores() > targetScore)
                .min(Comparator.comparingLong(e -> e.getKey().getScores()))
                .map(this::copyEntry)
                .orElse(null);
    }

    public void add(Customer customer, String data) {
        Customer copy = new Customer(customer.getId(), customer.getName(), customer.getScores());
        entries.add(new AbstractMap.SimpleEntry<>(copy, data));
    }

    private Map.Entry<Customer, String> copyEntry(Map.Entry<Customer, String> entry) {
        Customer original = entry.getKey();
        Customer copy = new Customer(original.getId(), original.getName(), original.getScores());
        return new AbstractMap.SimpleEntry<>(copy, entry.getValue());
    }
}
