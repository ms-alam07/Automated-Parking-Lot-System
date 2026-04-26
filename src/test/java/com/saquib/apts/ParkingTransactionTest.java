package com.saquib.apts;

import com.saquib.apts.DTO.VehicleEntryRequest;
import com.saquib.apts.DTO.VehicleExitRequest;
import com.saquib.apts.DTO.TicketResponse;
import com.saquib.apts.Entity.ParkingSpot;
import com.saquib.apts.Enums.SpotType;
import com.saquib.apts.Repository.ParkingSpotRepository;
import com.saquib.apts.Service.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ParkingTransactionTest {

    @Autowired
    private TicketService ticketService;

    @Autowired
    private ParkingSpotRepository spotRepository;

    @BeforeEach
    @Commit
    void setup() {
        // create one COMPACT spot before each test
        ParkingSpot spot = new ParkingSpot();
        spot.setSpotNumber("A1");
        spot.setSpotType(SpotType.COMPACT);
        spot.setAvailable(true);
        spotRepository.save(spot);
    }

    @Test
    void vehicleEntry_success() {
        VehicleEntryRequest request = new VehicleEntryRequest();
        request.setVehicleNumber("MH12AB1234");
        request.setSpotType(SpotType.COMPACT);

        TicketResponse response = ticketService.vehicleEntry(request);

        assertNotNull(response.getTicketNumber());
        assertEquals("MH12AB1234", response.getVehicleNumber());
        assertEquals("A1", response.getSpotNumber());
    }

    @Test
    void vehicleEntry_alreadyParked_throwsException() {
        VehicleEntryRequest request = new VehicleEntryRequest();
        request.setVehicleNumber("MH12AB1234");
        request.setSpotType(SpotType.COMPACT);

        ticketService.vehicleEntry(request);

        assertThrows(IllegalArgumentException.class, () ->
                ticketService.vehicleEntry(request));
    }

    @Test
    void vehicleEntry_noSpotAvailable_throwsException() {
        // fill the only spot
        VehicleEntryRequest request1 = new VehicleEntryRequest();
        request1.setVehicleNumber("MH12AB1234");
        request1.setSpotType(SpotType.COMPACT);
        ticketService.vehicleEntry(request1);

        // second vehicle tries to enter
        VehicleEntryRequest request2 = new VehicleEntryRequest();
        request2.setVehicleNumber("MH12AB5678");
        request2.setSpotType(SpotType.COMPACT);

        assertThrows(IllegalArgumentException.class, () ->
                ticketService.vehicleEntry(request2));
    }

    @Test
    void vehicleExit_invalidTicket_throwsException() {
        VehicleExitRequest request = new VehicleExitRequest();
        request.setTicketNumber("TKT-INVALID-0000");

        assertThrows(Exception.class, () ->
                ticketService.vehicleExit(request));
    }

    @Test
    void vehicleExit_alreadyExited_throwsException() {
        VehicleEntryRequest entryRequest = new VehicleEntryRequest();
        entryRequest.setVehicleNumber("MH12AB1234");
        entryRequest.setSpotType(SpotType.COMPACT);
        TicketResponse ticket = ticketService.vehicleEntry(entryRequest);

        VehicleExitRequest exitRequest = new VehicleExitRequest();
        exitRequest.setTicketNumber(ticket.getTicketNumber());
        ticketService.vehicleExit(exitRequest); // first exit

        assertThrows(IllegalArgumentException.class, () ->
                ticketService.vehicleExit(exitRequest)); // second exit
    }

    @Test
    void vehicleEntry_concurrentRequests_onlyOneGetsSpot() throws InterruptedException {
        int threadCount = 2;
        CountDownLatch latch = new CountDownLatch(1);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            final String vehicleNumber = "MH12AB000" + i;
            executor.submit(() -> {
                try {
                    latch.await(); // all threads start at same time
                    VehicleEntryRequest request = new VehicleEntryRequest();
                    request.setVehicleNumber(vehicleNumber);
                    request.setSpotType(SpotType.COMPACT);
                    ticketService.vehicleEntry(request);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            });
        }

        latch.countDown(); // release all threads simultaneously
        executor.shutdown();
        Thread.sleep(3000); // wait for threads to finish

        // only one should succeed, one should fail
        assertEquals(1, successCount.get());
        assertEquals(1, failCount.get());
    }
}