package com.videochat.videochatrecommendation2.service;

import com.videochat.videochatrecommendation2.model.Interests;
import com.videochat.videochatrecommendation2.model.User;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class MatchingService {
    private final static int HIGH = 3;
    private final static int MEDIUM = 2;
    private final static int LOW = 1;
    private final static int MIN_TO_MATCH = 2;

    private final ConcurrentLinkedQueue<User> waitingUsers = new ConcurrentLinkedQueue<>();
    private final ReentrantLock waitingUsersLock = new ReentrantLock();

    private final ConcurrentLinkedQueue<User> usersToJustStart = new ConcurrentLinkedQueue<>();
    private final ReentrantLock usersToJustStartLock = new ReentrantLock();

    private final ConcurrentHashMap<String, CompletableFuture<User>> waitingUserNotification = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(20);


    public void terminateMatchingIfInProcess(String userName) {
        waitingUsersLock.lock();
        usersToJustStartLock.lock();
        try {
            waitingUsers.removeIf(user -> user.username().equals(userName));
            usersToJustStart.removeIf(user -> user.username().equals(userName));
        } finally {
            waitingUsersLock.unlock();
            usersToJustStartLock.unlock();
        }
    }

    // CompletableFuture will contain the peer of current user
    public CompletableFuture<User> findPeerForUser(User user) {
        CompletableFuture<User> userNotification = new CompletableFuture<>();
        waitingUserNotification.put(user.username(), userNotification);

        searchForPeerWithAttempts(user);
        return userNotification;
    }

    private void searchForPeerWithAttempts(User user) {
        boolean firstAttemptSuccess = processFirstAttempt(user);
        if (!firstAttemptSuccess) {
            processSecondAttempt(user);
        }
    }

    private boolean processFirstAttempt(User user) {
        waitingUsersLock.lock();

        boolean firstAttemptFound = false;

        // first search
        Optional<User> firstAttemptPeer = searchForMatchingPeer(user);
        if (firstAttemptPeer.isPresent()) {
            firstAttemptFound = true;
            notifyMyselfAndPeer(user, firstAttemptPeer.get());
        } else {
            // put the user to the waiting queue
            waitingUsers.add(user);
        }

        waitingUsersLock.unlock();
        return firstAttemptFound;
    }

    private void processSecondAttempt(User user) {
        scheduler.schedule(() -> {
            waitingUsersLock.lock();

            // check if user has not been already removed from the waiting queue
            if (waitingUsers.contains(user)) {
                Optional<User> secondAttemptPeer = searchForMatchingPeer(user);

                if (secondAttemptPeer.isPresent())
                    notifyMyselfAndPeer(user, secondAttemptPeer.get());
                else {
                    Optional<User> randomUser = searchForRandomUserToJustStart();

                    if (randomUser.isPresent())
                        notifyMyselfAndPeer(user, randomUser.get());
                    else
                        usersToJustStart.add(user);
                }
            }

            waitingUsersLock.unlock();

        }, 10, TimeUnit.SECONDS);
    }

    private void notifyMyselfAndPeer(User me, User peer) {
        waitingUserNotification.get(me.username()).complete(peer);
        waitingUserNotification.get(peer.username()).complete(me);
    }

    private Optional<User> searchForRandomUserToJustStart() {
        usersToJustStartLock.lock();
        User peer = usersToJustStart.poll();

        if (peer == null) {
            usersToJustStartLock.unlock();
            return Optional.empty();
        }

        // Do not need to remove current user from the waitingUsers because it does not contain him.
        // It will be added to this queue only if the search in this method was unsuccessful.

        usersToJustStartLock.unlock();
        return Optional.of(peer);
    }

    private Optional<User> searchForMatchingPeer(User user) {
        if (waitingUsers.isEmpty()) {
            return Optional.empty();
        }

        User maxPeer = waitingUsers.peek();
        int maxScore = 0;

        for (var peer : waitingUsers) {
            int matchScore = calculateScore(user, peer);

            if (matchScore > maxScore) {
                maxScore = matchScore;
                maxPeer = peer;
            }
        }

        if (maxScore < MIN_TO_MATCH) {
            return Optional.empty();
        }

        waitingUsers.remove(maxPeer);
        return Optional.of(maxPeer);
    }

    private int calculateScore(User firstPeer, User secondPeer) {
        int scoreMatch = 0;

        // Preferences match
        if (firstPeer.preferredGender().equals(secondPeer.preferredGender())) {
            scoreMatch += HIGH;
        }
        // Preferences match only for one of the peers
        else if (firstPeer.preferredGender().equals(secondPeer.myGender()) ||
                secondPeer.preferredGender().equals(firstPeer.myGender())) {
            scoreMatch += LOW;
        }

        var numberOfMatchingInterests = getNumberOfMatchingInterests(firstPeer.interests(), secondPeer.interests());
        scoreMatch += numberOfMatchingInterests * MEDIUM;
        return scoreMatch;
    }

    private int getNumberOfMatchingInterests(List<Interests> firstPeerInterests, List<Interests> secondPeerInterests) {
        var firstSet = new HashSet<>(firstPeerInterests);
        return (int) secondPeerInterests
                .stream()
                .filter(firstSet::contains)
                .count();
    }
}
