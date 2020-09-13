package com.fest.pecfestBackend.service;

import com.fest.pecfestBackend.entity.Team;
import com.fest.pecfestBackend.entity.User;
import com.fest.pecfestBackend.repository.EventRepo;
import com.fest.pecfestBackend.repository.TeamRepo;
import com.fest.pecfestBackend.repository.UserRepo;
import com.fest.pecfestBackend.response.WrapperResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class EventRegistrationService {
    @Autowired
    UserRepo userRepo;
    @Autowired
    TeamRepo teamRepo;
    @Autowired
    private SessionService sessionService;
    @Autowired
    private EventRepo eventRepo;

    public WrapperResponse registerTeamForAnEvent(Long eventId, List<String> pecFestIds, String teamName, String sessionId) {
        if(Objects.isNull(sessionId)||sessionId.length()<2)
        return WrapperResponse.builder().httpStatus(HttpStatus.FORBIDDEN).statusMessage("Please Log in first").build();
        if(Objects.isNull(pecFestIds)||pecFestIds.size()<1)
            return WrapperResponse.builder().httpStatus(HttpStatus.FORBIDDEN).statusMessage("Empty PECFEST Usernames' list").build();
        if(pecFestIds.parallelStream().distinct().count() <pecFestIds.size())
            return WrapperResponse.builder().httpStatus(HttpStatus.FORBIDDEN).statusMessage("All PECFEST Usernames must be unique").build();
        User user=sessionService.verifySessionId(sessionId);
        if(Objects.isNull(user)) {
            return WrapperResponse.builder().httpStatus(HttpStatus.FORBIDDEN).statusMessage("Please Log in first").build();
        }
        else{
            if(!eventRepo.existsByEventID(eventId))
                return WrapperResponse.builder().statusMessage("No such event exists").httpStatus(HttpStatus.BAD_REQUEST).build();

           String inValidPecFestIds=pecFestIds.parallelStream().filter(pecFestId->!userRepo.existsByPecFestIdAndIsVerified(pecFestId,true)).collect(Collectors.joining(", "));
           if(StringUtils.isAllEmpty(inValidPecFestIds))
           {
                if(teamRepo.existsByTeamNameAndEventId(teamName,eventId)) {
                    return WrapperResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).statusMessage("Team Name already exists").build();
                }
                else{
                    teamRepo.save(Team.builder().eventId(eventId).leaderPecFestId(pecFestIds.get(0)).memberPecFestIdList(String.join(",", pecFestIds)).teamName(teamName)
                            .leaderId(userRepo.findByPecFestId(pecFestIds.get(0)).getId())
                            .build());
                    return WrapperResponse.builder().statusMessage("Event Registration is successful").build();
                }
           }
           else{
               return WrapperResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).statusMessage("Invalid PECFEST ids: "+inValidPecFestIds).build();
           }
        }

    }

    public WrapperResponse registerAnIndividual(Long eventId, String sessionId) {
        if(Objects.isNull(sessionId)||sessionId.length()<2)
            return WrapperResponse.builder().httpStatus(HttpStatus.FORBIDDEN).statusMessage("Please Log in first").build();
        User user=sessionService.verifySessionId(sessionId);
        if(Objects.isNull(user)) {
            return WrapperResponse.builder().httpStatus(HttpStatus.FORBIDDEN).statusMessage("Please Log in first").build();
        }
        else{
            if(teamRepo.existsByTeamNameAndEventId(user.getPecFestId(),eventId))
                return WrapperResponse.builder().httpStatus(HttpStatus.BAD_REQUEST).statusMessage("You have already been registered").build();
            if(!eventRepo.existsByEventID(eventId))
                return WrapperResponse.builder().statusMessage("No such event exists").httpStatus(HttpStatus.BAD_REQUEST).build();
            teamRepo.save(Team.builder().eventId(eventId).leaderPecFestId(user.getPecFestId()).memberPecFestIdList(user.getPecFestId()).teamName(user.getPecFestId())
                    .leaderId(user.getId())
                    .build());
            return WrapperResponse.builder().statusMessage("Event Registration is successful").build();
        }
    }
}
