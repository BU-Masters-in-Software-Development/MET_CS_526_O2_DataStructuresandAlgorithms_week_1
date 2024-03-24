"""
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

"""


class PickupSnapshotNode:
    def __init__(self, location_id: int, timestamp: int, next_node: 'PickupSnapshotNode' = None):
        self.id = location_id
        self.timestamp = timestamp
        self.next = next_node

    def get_id(self):
        return self.id

    def get_timestamp(self):
        return self.timestamp

    def get_next(self):
        return self.next

    def set_next(self, node: 'PickupSnapshotNode'):
        self.next = node


class InvalidRouteError(Exception):
    def __init__(self):
        super().__init__()


def detect_cyclic_route(start: PickupSnapshotNode):
    if not start:
        return None

    visited_nodes = {}
    current_node = start
    last_timestamp = current_node.get_timestamp()

    while current_node:
        node_id = current_node.get_id()
        timestamp = current_node.get_timestamp()

        if timestamp < last_timestamp:
            raise InvalidRouteError()

        if node_id in visited_nodes:
            # Start of the cycle detected
            cycle_time = timestamp - visited_nodes[node_id]
            # To check for timestamp order within the cycle
            cycle_node = current_node.get_next()
            while cycle_node and cycle_node != current_node:
                if cycle_node.get_timestamp() <= last_timestamp:
                    raise InvalidRouteError()
                last_timestamp = cycle_node.get_timestamp()
                cycle_node = cycle_node.get_next()
            return cycle_time

        visited_nodes[node_id] = timestamp
        last_timestamp = timestamp
        current_node = current_node.get_next()

    return None


class TestingBase:
    def print_test_result(self, test_name, result):
        color = "\033[92m" if result else "\033[91m"
        reset = "\033[0m"
        print(f"{color}[{result}] {test_name}{reset}")

    def test_answer(self, test_name, result, expected):
        if result == expected:
            self.print_test_result(test_name, True)
        else:
            self.print_test_result(test_name, False)
            print(f"Expected: {expected} \nGot:      {result}")


# Unit Testing Class
class TestRideSnapshotNode(TestingBase):
    def run_unit_tests(self):
        self.test_detect_cyclic_ride()
        self.test_detect_cyclic_ride_no_cycle()
        self.test_back_in_time_1()
        self.test_back_in_time_2()
        self.test_detects_cyclic_ride_2()
        self.test_detects_non_cyclic_ride_2()
        self.test_back_in_time_after_cycle()

    def test_detect_cyclic_ride(self):
        # Create nodes for a cyclic ride
        node4 = PickupSnapshotNode(12345, 1685288860, None)
        node3 = PickupSnapshotNode(21341, 1685288560, node4)
        node2 = PickupSnapshotNode(12345, 1685288260, node3)
        node1 = PickupSnapshotNode(32144, 1685287960, node2)

        # Call the detect_cyclic_ride function
        result = detect_cyclic_route(node1)
        # Cycle is node 2 -> node 3 -> node 4
        expected_answer = 1685288860 - 1685288260

        self.test_answer("test_detect_cyclic_ride_with_cycle", result, expected_answer)

    def test_detect_cyclic_ride_no_cycle(self):
        # Create nodes for a non-cyclic ride
        node4 = PickupSnapshotNode(12345, 1685288860, None)
        node3 = PickupSnapshotNode(21341, 1685288560, node4)
        node2 = PickupSnapshotNode(32144, 1685288260, node3)
        node1 = PickupSnapshotNode(12237, 1685287960, node2)

        # Call the detect_cyclic_ride function
        result = detect_cyclic_route(node1)

        # Assert that no cycle is detected (result should be None or False)
        self.test_answer("test_detect_cyclic_ride_no_cycle", result, None)


    def test_back_in_time_1(self):
        node1 = PickupSnapshotNode(1, 1, None)
        node2 = PickupSnapshotNode(2, 2, None)
        node3 = PickupSnapshotNode(3, 3, None)
        node4 = PickupSnapshotNode(4, 4, None)
        node5 = PickupSnapshotNode(5, 3, None)

        node1.set_next(node2)
        node2.set_next(node3)
        node3.set_next(node4)
        node4.set_next(node5)

        try:
            result = detect_cyclic_route(node1)
            self.print_test_result("testBackInTime1", False)
            print("Expected to get error but got:      ", result)
        except InvalidRouteError:
            self.print_test_result("testBackInTime1", True)

    def test_back_in_time_2(self):
        node1 = PickupSnapshotNode(1, 1, None)
        node2 = PickupSnapshotNode(2, 2, None)
        node3 = PickupSnapshotNode(3, 3, None)
        node4 = PickupSnapshotNode(4, 4, None)
        node5 = PickupSnapshotNode(5, 5, None)

        node1.set_next(node2)
        node2.set_next(node3)
        node3.set_next(node4)
        node4.set_next(node5)
        node5.set_next(node1)

        try:
            result = detect_cyclic_route(node1)
            self.print_test_result("testBackInTime2", False)
            print("Expected to get error but got:", result)
        except InvalidRouteError:
            self.print_test_result("testBackInTime2", True)

    def test_back_in_time_after_cycle(self):
        node1 = PickupSnapshotNode(1, 1, None)
        node2 = PickupSnapshotNode(2, 2, None)
        node3 = PickupSnapshotNode(3, 3, None)
        node4 = PickupSnapshotNode(1, 4, None)
        node5 = PickupSnapshotNode(5, 1, None)

        node1.set_next(node2)
        node2.set_next(node3)
        node3.set_next(node4)
        node4.set_next(node5)

        try:
            result = detect_cyclic_route(node1)
            self.print_test_result("testBackInTimeAfterCycle", False)
            print("Expected to get error but got:", result)
        except InvalidRouteError:
            self.print_test_result("testBackInTimeAfterCycle", True)

    def test_detects_cyclic_ride_2(self):
        node1 = PickupSnapshotNode(1, 1, None)
        node2 = PickupSnapshotNode(2, 2, None)
        node3 = PickupSnapshotNode(3, 3, None)
        node4 = PickupSnapshotNode(4, 4, None)
        node5 = PickupSnapshotNode(5, 5, None)
        node6 = PickupSnapshotNode(1, 6, None)

        node1.set_next(node2)
        node2.set_next(node3)
        node3.set_next(node4)
        node4.set_next(node5)
        node5.set_next(node6)

        result = detect_cyclic_route(node1)
        expected_answer = 5
        self.test_answer("testDetectsCyclicRide2", expected_answer, result)

    def test_detects_non_cyclic_ride_2(self):
        node1 = PickupSnapshotNode(1, 3, None)
        node2 = PickupSnapshotNode(2, 2, None)
        node3 = PickupSnapshotNode(3, 4, None)
        node4 = PickupSnapshotNode(4, 5, None)
        node5 = PickupSnapshotNode(1, 6, None)
        node6 = PickupSnapshotNode(2, 1, None)

        node6.set_next(node2)
        node2.set_next(node1)
        node1.set_next(node3)
        node3.set_next(node4)
        node5.set_next(node1)

        result = detect_cyclic_route(node1)
        expected_answer = None
        self.test_answer("testDetectsNonCyclicRide2", expected_answer, result)


if __name__ == '__main__':
    test_runner = TestRideSnapshotNode()
    test_runner.run_unit_tests()
