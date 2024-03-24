package student;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 You are employed by a new company called "AlgoZon," with the task of enhancing the package
 delivery process. The company aims to identify whether a driver has taken a circular route during
 package deliveries. Your responsibility is to determine if, at any point in the trip, the driver returned
 to a location they had previously visited.

 The monitoring system currently in use at AlgoZon tracks routes by drivers as a Linked List, where
 each node has an ID (integer) and a timestamp (in Unix format).
 Each node represents a package pickup zone where a driver gets packages.

 To check for inefficiency, AlgoZon has assigned you the task of writing a function that will detect
 whether a driver has returned to the same pickup zone. Additionally, you must also audit whether
 the linked list of destinations is in chronological order; in order words, we want to detect an error
 if a driver reaches a destination before the previous one in the linked list.

 Write a function that takes in the first node in the monitoring linked list and returns the total time
 for the cycle, or null if there isn’t one. The function should throw an InvalidRouteError if any
 destination in the linked list is reached before its predecessor (even after a cycle).

 The code for a node class and InvalidRouteError class is provided below.

 Code Author: Adrian Everardo Ortiz

 */

/*
 * This class is designed to detect circular routes in a package delivery system.
 * Each package pickup zone is represented by a node in a linked list, with an ID and timestamp.
 */

public class AlgoZon {

    /*
     * Node class representing each pickup snapshot with ID and timestamp.
     */
    public static class PickupSnapshotNode {
        private final int id;
        private final int timestamp;
        private PickupSnapshotNode next;

        public PickupSnapshotNode(int locationId, int timestamp, PickupSnapshotNode nextNode) {
            this.id = locationId;
            this.timestamp = timestamp;
            this.next = nextNode;
        }

        public int getId() {
            return this.id;
        }

        public int getTimestamp() {
            return this.timestamp;
        }

        public PickupSnapshotNode getNext() {
            return this.next;
        }

        public void setNext(PickupSnapshotNode next) {
            this.next = next;
        }
    }

    /*
     * Exception class to indicate invalid route errors.
     */

    public static class InvalidRouteError extends Exception {
        public InvalidRouteError(String message) {
            super(message);
        }

        public InvalidRouteError() {
            super("Invalid route found!");
        }
    }


    public static Integer detectCyclicRoute(PickupSnapshotNode start) throws InvalidRouteError {
        // Map to store the last visited timestamp for each node ID
        Map<Integer, Integer> visitedNodes = new HashMap<>();
        // Current node being processed
        PickupSnapshotNode current = start;
        // Initialize the last timestamp seen to the minimum integer value
        int lastTimestamp = Integer.MIN_VALUE;
        // Variable to track the total time for cyclic rides
        int totalTime = 0;

        // Iterate through the linked list until the end is reached
        while (current != null) {
            // Extract the ID and timestamp of the current node
            int currentId = current.getId();
            int currentTimestamp = current.getTimestamp();

            // Check for chronological order before proceeding with cycle detection.
            if (currentTimestamp < lastTimestamp) {
                throw new InvalidRouteError("Timestamps are not in chronological order.");
            }
            lastTimestamp = currentTimestamp;

            // Check if we've encountered this node before to detect a cycle.
            if (visitedNodes.containsKey(currentId)) {
                // If the node with the same ID has different timestamps, update the timestamp if it's consecutive.
                if (currentTimestamp > visitedNodes.get(currentId)) {
                    // Calculate the time spent at this node and add it to the total time
                    totalTime += currentTimestamp - visitedNodes.get(currentId);
                    // Update the timestamp for this node ID
                    visitedNodes.put(currentId, currentTimestamp);
                } else {
                    // If the timestamp is not consecutive, throw an error
                    throw new InvalidRouteError("Node with the same ID but different timestamp encountered.");
                }
            } else {
                // Store the current timestamp as the first visit time for this node ID
                visitedNodes.put(currentId, currentTimestamp);
            }

            // Move to the next node in the linked list
            current = current.getNext();
        }

        // Check if any cyclic route was detected
        if (totalTime > 0) {
            return totalTime;
        } else {
            // No cycle detected, return null.
            return null;
        }
    }


    /*
    DO NOT EDIT BELOW THIS
    Below is the unit testing suite for this file.
    It provides all the tests that your code must pass to get full credit.
    */
    private static void printTestResult(String testName, boolean result) {
        String color = result ? "\033[92m" : "\033[91m";
        String reset = "\033[0m";
        System.out.printf("%s[%b] %s%s\n", color, result, testName, reset);
    }

    private static void testAnswer(String testName, Integer expected, Integer actual) {
        if (Objects.equals(actual, expected)) {
            printTestResult(testName, true);
        }
        else {
            printTestResult(testName, false);
            System.out.printf("Expected: %s \nGot:      %s\n", expected, actual);
        }
    }

    public static void runTests() throws InvalidRouteError {
        testDetectsCyclicRide();
        testDetectsNonCyclicRide();
        testBackInTime1();
        testBackInTime2();
        testDetectsCyclicRide2();
        testDetectsNonCyclicRide2();
        testBackInTimeAfterCycle();
    }

    private static void testDetectsCyclicRide() throws InvalidRouteError {
        // Create nodes for a cyclic ride
        PickupSnapshotNode node4 = new PickupSnapshotNode(12345, 1685288860, null);
        PickupSnapshotNode node3 = new PickupSnapshotNode(21341, 1685288560, node4);
        PickupSnapshotNode node2 = new PickupSnapshotNode(12345, 1685288260, node3);
        PickupSnapshotNode node1 = new PickupSnapshotNode(32144, 1685287960, node2);

        // Call the detect_cyclic_ride function
        Integer result = detectCyclicRoute(node1);
        // Cycle is node 2 -> node 3 -> node 4
        Integer expected_answer = 1685288860 - 1685288260;
        testAnswer("testDetectsCyclicRide", expected_answer, result);
    }

    private static void testDetectsNonCyclicRide() throws InvalidRouteError {
        // Create nodes for a cyclic ride
        PickupSnapshotNode node4 = new PickupSnapshotNode(12345, 1685288860, null);
        PickupSnapshotNode node3 = new PickupSnapshotNode(21341, 1685288560, node4);
        PickupSnapshotNode node2 = new PickupSnapshotNode(32144, 1685288260, node3);
        PickupSnapshotNode node1 = new PickupSnapshotNode(12237, 1685287960, node2);

        // Call the detect_cyclic_ride function
        Integer result = detectCyclicRoute(node1);
        Integer expected_answer = null;
        testAnswer("testDetectsNonCyclicRide", expected_answer, result);
    }

    private static void testBackInTime1() {
        // Create nodes for a cyclic ride
        PickupSnapshotNode node1 = new PickupSnapshotNode(1, 1, null);
        PickupSnapshotNode node2 = new PickupSnapshotNode(2, 2, null);
        PickupSnapshotNode node3 = new PickupSnapshotNode(3, 3, null);
        PickupSnapshotNode node4 = new PickupSnapshotNode(4, 4, null);
        PickupSnapshotNode node5 = new PickupSnapshotNode(5, 3, null);

        node1.setNext(node2);
        node2.setNext(node3);
        node3.setNext(node4);
        node4.setNext(node5);

        // Call the detect_cyclic_ride function
        Integer result;
        try {
            result = detectCyclicRoute(node1);
        } catch (InvalidRouteError e) {
            printTestResult("testBackInTime1", true);
            return;
        }
        printTestResult("testBackInTime1", false);
        System.out.printf("Expected to get error but got:      %s\n", result);
    }

    private static void testBackInTime2() {
        // Create nodes for a cyclic ride
        PickupSnapshotNode node1 = new PickupSnapshotNode(1, 1, null);
        PickupSnapshotNode node2 = new PickupSnapshotNode(2, 2, null);
        PickupSnapshotNode node3 = new PickupSnapshotNode(3, 3, null);
        PickupSnapshotNode node4 = new PickupSnapshotNode(4, 4, null);
        PickupSnapshotNode node5 = new PickupSnapshotNode(5, 5, null);

        node1.setNext(node2);
        node2.setNext(node3);
        node3.setNext(node4);
        node4.setNext(node5);
        node5.setNext(node1);   // Go back in time INVALID

        // Call the detect_cyclic_ride function
        Integer result;
        try {
            result = detectCyclicRoute(node1);
        } catch (InvalidRouteError e) {
            printTestResult("testBackInTime2", true);
            return;
        }
        printTestResult("testBackInTime2", false);
        System.out.printf("Expected to get error but got:      %s\n", result);
    }

    private static void testBackInTimeAfterCycle() {
        // Create nodes for a cyclic ride
        PickupSnapshotNode node1 = new PickupSnapshotNode(1, 1, null);
        PickupSnapshotNode node2 = new PickupSnapshotNode(2, 2, null);
        PickupSnapshotNode node3 = new PickupSnapshotNode(3, 3, null);
        PickupSnapshotNode node4 = new PickupSnapshotNode(1, 4, null);
        PickupSnapshotNode node5 = new PickupSnapshotNode(5, 1, null);

        node1.setNext(node2);
        node2.setNext(node3);
        node3.setNext(node4);
        node4.setNext(node5);

        // Call the detect_cyclic_ride function
        Integer result;
        try {
            result = detectCyclicRoute(node1);
        } catch (InvalidRouteError e) {
            printTestResult("testBackInTimeAfterCycle", true);
            return;
        }
        printTestResult("testBackInTimeAfterCycle", false);
        System.out.printf("Expected to get error but got:      %s\n", result);
    }

    private static void testDetectsCyclicRide2() throws InvalidRouteError {
        // Create nodes for a cyclic ride
        PickupSnapshotNode node1 = new PickupSnapshotNode(1, 1, null);
        PickupSnapshotNode node2 = new PickupSnapshotNode(2, 2, null);
        PickupSnapshotNode node3 = new PickupSnapshotNode(3, 3, null);
        PickupSnapshotNode node4 = new PickupSnapshotNode(4, 4, null);
        PickupSnapshotNode node5 = new PickupSnapshotNode(5, 5, null);
        PickupSnapshotNode node6 = new PickupSnapshotNode(1, 6, null);


        node1.setNext(node2);
        node2.setNext(node3);
        node3.setNext(node4);
        node4.setNext(node5);
        node5.setNext(node6);

        // Call the detect_cyclic_ride function
        Integer result = detectCyclicRoute(node1);
        // Cycle is 1 -> 2 -> 3 -> 4 -> 5 -> 1
        Integer expected_answer = 5;
        testAnswer("testDetectsCyclicRide2", expected_answer, result);
    }

    private static void testDetectsNonCyclicRide2() throws InvalidRouteError {
        // Create nodes for a cyclic ride
        PickupSnapshotNode node1 = new PickupSnapshotNode(1, 3, null);
        PickupSnapshotNode node2 = new PickupSnapshotNode(2, 2, null);
        PickupSnapshotNode node3 = new PickupSnapshotNode(3, 4, null);
        PickupSnapshotNode node4 = new PickupSnapshotNode(4, 5, null);
        PickupSnapshotNode node5 = new PickupSnapshotNode(1, 6, null);
        PickupSnapshotNode node6 = new PickupSnapshotNode(2, 1, null);

        node6.setNext(node2);
        node2.setNext(node1);
        node1.setNext(node3);
        node3.setNext(node4);
        node5.setNext(node1);

        // Call the detect_cyclic_ride function
        Integer result = detectCyclicRoute(node1);
        Integer expected_answer = null;
        testAnswer("testDetectsNonCyclicRide2", expected_answer, result);
    }

    public static void main(String[] args) throws InvalidRouteError {
        AlgoZon.runTests();
    }
}
