package allocationManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AllocationManager {
    private Set<String> UserName;
    private boolean isAdminConnected;
    private Map<Integer, UserRequest> requestIdToUserRequest;

    public AllocationManager(Set<String> userName, boolean isAdminConnected, Map<Integer, UserRequest> requestIdToUserRequest) {
        UserName = userName;
        this.isAdminConnected = isAdminConnected;
        this.requestIdToUserRequest = requestIdToUserRequest;
    }

    public AllocationManager() {
        this.UserName = new HashSet<>();
        this.isAdminConnected = false;
        this.requestIdToUserRequest = new HashMap<>();
    }

    public Set<String> getUserName() {
        return UserName;
    }

    public void setUserName(Set<String> userName) {
        UserName = userName;
    }

    public boolean isAdminConnected() {
        return isAdminConnected;
    }

    public void setAdminConnected(boolean adminConnected) {
        isAdminConnected = adminConnected;
    }

    public Map<Integer, UserRequest> getRequestIdToUserRequest() {
        return requestIdToUserRequest;
    }

    public void setRequestIdToUserRequest(Map<Integer, UserRequest> requestIdToUserRequest) {
        this.requestIdToUserRequest = requestIdToUserRequest;
    }
}
