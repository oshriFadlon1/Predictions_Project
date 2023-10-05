package allocationManager;

import dtos.admin.DtoAllSimulationRequests;
import dtos.admin.DtoSimulationsRequest;
import dtos.requestInfo.DtoTerminationSimulationInfo;
import dtos.user.DtoRequestForAdmin;
import termination.Termination;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class AllocationManager {
    private Set<String> userName;
    private AtomicBoolean isAdminConnected;
    private Map<Integer, UserRequest> requestIdToUserRequest;
    private int countNumberOfRequest;
    private final Object lockOfAddRequests;
    private final Object lockForAddingUser;


    public AllocationManager(Set<String> userName, AtomicBoolean isAdminConnected, Map<Integer, UserRequest> requestIdToUserRequest) {
        this.userName = userName;
        this.isAdminConnected = isAdminConnected;
        this.requestIdToUserRequest = requestIdToUserRequest;
        countNumberOfRequest = 1;
        this.lockOfAddRequests = new Object();
        this.lockForAddingUser = new Object();
    }

    public AllocationManager() {
        this.userName = new HashSet<>();
        this.isAdminConnected = new AtomicBoolean(false);
        this.requestIdToUserRequest = new HashMap<>();
        this.countNumberOfRequest = 1;
        this.lockOfAddRequests = new Object();
        this.lockForAddingUser = new Object();
    }

    public Set<String> getUserName() {
        return userName;
    }

    public void setUserName(Set<String> userName) {
        this.userName = userName;
    }

    public AtomicBoolean getIsAdminConnected() {
        return isAdminConnected;
    }

    public void setIsAdminConnected(AtomicBoolean isAdminConnected) {
        this.isAdminConnected = isAdminConnected;
    }

    public Map<Integer, UserRequest> getRequestIdToUserRequest() {
        return requestIdToUserRequest;
    }

    public void setRequestIdToUserRequest(Map<Integer, UserRequest> requestIdToUserRequest) {
        this.requestIdToUserRequest = requestIdToUserRequest;
    }

    public String addNameToSet(String name){
        if (!isNameInSet(name))
        {
            synchronized (lockForAddingUser){
                if (!isNameInSet(name)) {
                    userName.add(name);
                }
            }
            return "user add successfully";
        }
        return "user name is used already";
    }

    private boolean isNameInSet(String name) {
        return userName.contains(name);
    }

    public String removeNameFromSet(String name){
        if (isNameInSet(name)){
            synchronized (lockForAddingUser) {
                if (isNameInSet(name)) {
                    this.userName.remove(name);
                }
            }
            return "user removed successfully";
        }
        return "user name is not in exist";
    }

    public boolean addRequest(DtoRequestForAdmin dtoRequestForAdmin){
        synchronized (this.lockOfAddRequests){
            this.requestIdToUserRequest.put(this.countNumberOfRequest,
                    new UserRequest(this.countNumberOfRequest,
                            dtoRequestForAdmin.getSimulationName(),
                            dtoRequestForAdmin.getUserName(),
                            dtoRequestForAdmin.getRequestedRuns(),
                            new Termination(dtoRequestForAdmin.getTermination().getTicks(),
                                    dtoRequestForAdmin.getTermination().getSeconds(),
                                    dtoRequestForAdmin.getTermination().isFreeChoice()),
                            dtoRequestForAdmin.getRequestStatus(),
                            0,
                            0));
            this.countNumberOfRequest++;
        }
        return true;
    }

    public DtoAllSimulationRequests fetchRequestsForUser(String name){
        Map<Integer, DtoSimulationsRequest> requestMap = new HashMap<>();
        synchronized (lockOfAddRequests){
            for (Integer integer :this.requestIdToUserRequest.keySet()) {
                UserRequest userRequest = this.requestIdToUserRequest.get(integer);
                if (userRequest.getUserName().equalsIgnoreCase(name)){
                    requestMap.put(integer, convertUserRequestToDtoSimulationsRequest(userRequest));
                }
            }
        }
        return new DtoAllSimulationRequests(requestMap);
    }

    private DtoSimulationsRequest convertUserRequestToDtoSimulationsRequest(UserRequest userRequest){
        return new DtoSimulationsRequest(userRequest.getRequestId(),
                userRequest.getSimulationName(),
                userRequest.getUserName(),
                userRequest.getRequestedRuns(),
                new DtoTerminationSimulationInfo(userRequest.getTermination().getTicks(), userRequest.getTermination().getSeconds(), userRequest.getTermination().getBUser()),
                userRequest.getRequestStatus(),
                userRequest.getCurrentRuns(),
                userRequest.getFinishedRuns());
    }

    public DtoAllSimulationRequests fetchAllRequests() {
        Map<Integer, DtoSimulationsRequest> allRequests = new HashMap<>();
        synchronized (lockOfAddRequests){
            for (Integer integer : this.requestIdToUserRequest.keySet()) {
                UserRequest userRequest = this.requestIdToUserRequest.get(integer);
                allRequests.put(integer,convertUserRequestToDtoSimulationsRequest(userRequest));
            }
        }
        return new DtoAllSimulationRequests(allRequests);
    }

    public String approveRequest(DtoSimulationsRequest dtoSimulationsRequest){
        synchronized (lockOfAddRequests){
            UserRequest userRequest = this.requestIdToUserRequest.get(dtoSimulationsRequest.getRequestId());
            userRequest.setRequestStatus(dtoSimulationsRequest.getRequestStatus());
        }
        return "approved";
    }
    public String rejectRequest(DtoSimulationsRequest dtoSimulationsRequest){
        synchronized (lockOfAddRequests) {
            UserRequest userRequest = this.requestIdToUserRequest.get(dtoSimulationsRequest.getRequestId());
            userRequest.setRequestStatus(dtoSimulationsRequest.getRequestStatus());
            userRequest.setCurrentRuns(dtoSimulationsRequest.getCurrentRuns());
            userRequest.setFinishedRuns(dtoSimulationsRequest.getFinishedRuns());
        }
        return "rejected";
    }
}
