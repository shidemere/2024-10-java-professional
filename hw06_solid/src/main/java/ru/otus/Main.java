package ru.otus;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        ATM atm = new ATM();

        // Добавляем банкноты в банкомат
        List<Banknote> banknotes = List.of(new Banknote(Denomination.EU_50), new Banknote(Denomination.EU_20));
        atm.putBanknotes(banknotes);
        log.info("Банкноты добавлены в банкомат. Сумма в банкомате: {}", atm.getRemainder());

        // Получаем сумму 70 евро
        List<Banknote> withdrawnBanknotes = atm.getSum(70L);
        log.info("Полученные при снятии с банкомата банкноты {}", withdrawnBanknotes);
        // Проверяем остаток в банкомате
        log.info("После снятия денег в банкомате осталось {}", atm.getRemainder());
    }
}
