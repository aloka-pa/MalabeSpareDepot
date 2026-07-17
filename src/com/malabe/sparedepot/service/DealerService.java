package com.malabe.sparedepot.service;

import com.malabe.sparedepot.model.Dealer;
import com.malabe.sparedepot.persistence.DealerRepository;
import com.malabe.sparedepot.util.ManualSorter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DealerService {
    private final DealerRepository dealerRepository;
    private final List<Dealer> dealers;
    private final Random random;

    public DealerService(DealerRepository dealerRepository) {
        this.dealerRepository = dealerRepository;
        this.dealers = new ArrayList<Dealer>();
        this.random = new Random();
        load();
    }

    public final void load() {
        dealers.clear();
        dealers.addAll(dealerRepository.load());
    }

    public List<Dealer> getAllSortedByLocation() {
        List<Dealer> copy = new ArrayList<Dealer>();
        for (int i = 0; i < dealers.size(); i++) {
            Dealer dealer = dealers.get(i);
            copy.add(new Dealer(dealer.getCode(), dealer.getName(), dealer.getPhone(), dealer.getLocation()));
        }
        ManualSorter.sortDealersByLocation(copy);
        return copy;
    }

    public List<Dealer> selectFourUniqueDealers() {
        List<Dealer> selected = new ArrayList<Dealer>();
        if (dealers.size() <= 4) {
            for (int i = 0; i < dealers.size(); i++) {
                selected.add(copyDealer(dealers.get(i)));
            }
            ManualSorter.sortDealersByLocation(selected);
            return selected;
        }

        boolean[] used = new boolean[dealers.size()];
        int safety = 0;
        while (selected.size() < 4 && safety < 1000) {
            int index = random.nextInt(dealers.size());
            if (!used[index]) {
                used[index] = true;
                selected.add(copyDealer(dealers.get(index)));
            }
            safety++;
        }
        ManualSorter.sortDealersByLocation(selected);
        return selected;
    }

    public void save() {
        try {
            dealerRepository.save(dealers);
        } catch (IOException ex) {
            // ignore
        }
    }

    private Dealer copyDealer(Dealer dealer) {
        return new Dealer(dealer.getCode(), dealer.getName(), dealer.getPhone(), dealer.getLocation());
    }
}
