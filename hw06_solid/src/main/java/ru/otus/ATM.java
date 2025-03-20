package ru.otus;

import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ATM {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Map<Denomination, Wallet> cellMap = Arrays.stream(Denomination.values())
            .collect(Collectors.toMap(denomination -> denomination, Wallet::emptyCell));

    public Long putBanknotes(List<Banknote> banknotes) {
        for (Banknote banknote : banknotes) {
            this.cellMap.get(banknote.denomination()).putBanknote(banknote);
        }
        Long sum = banknotes.stream().mapToLong(Banknote::getValue).sum();
        log.info("ATM filled: {} banknote(s) on sum {}", banknotes.size(), sum);
        return sum;
    }

    public List<Banknote> getSum(Long sum) {
        if (sum == null || sum < 1) {
            throw new IllegalArgumentException("Sum is NULL or less ONE. sum=[%s]".formatted(sum));
        }
        Set<Denomination> sortedDenomination = cellMap.keySet().stream()
                .sorted(Comparator.comparing(Denomination::getNum))
                .collect(Collectors.toCollection(LinkedHashSet::new))
                .reversed();

        var banknotesToOut = new ArrayList<Banknote>();
        for (Denomination denomination : sortedDenomination) {
            if (sum == 0) {
                break;
            }
            if (cellMap.get(denomination).isEmpty()) {
                continue;
            }
            var banknotePower = denomination.getNum();
            var banknoteNeedCount = sum / banknotePower;
            if (banknoteNeedCount > 0) {
                List<Banknote> addedCache = cellMap.get(denomination).getBanknotesOrAll(banknoteNeedCount);
                banknotesToOut.addAll(addedCache);
                sum %= addedCache.stream().mapToLong(Banknote::getValue).sum();
            }
        }
        if (sum != 0) {
            throw new CacheOutException("Can't return all sum");
        }

        return banknotesToOut;
    }

    public Long getRemainder() {
        return this.cellMap.values().stream().mapToLong(Wallet::getSumMoney).sum();
    }
}
