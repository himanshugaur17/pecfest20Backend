package com.fest.pecfestBackend.controller;

import com.fest.pecfestBackend.enums.Club;
import com.fest.pecfestBackend.enums.EventType;
import com.fest.pecfestBackend.request.EventRequest;
import com.fest.pecfestBackend.response.WrapperResponse;
import com.fest.pecfestBackend.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/event")
public class EventController {
    @Autowired
    private EventService eventService;
    @GetMapping
    public WrapperResponse getEventsByOrganizingClubName(@RequestParam Club clubName, @RequestParam EventType eventType){
        return eventService.getEventsByOrganizingClubName(clubName);
    }
    @GetMapping("/all")
    public WrapperResponse getAllEvents(){
        return eventService.getAllEvents();
    }
    @GetMapping("/club-wise")
    public WrapperResponse getAllEventsByClub(){
        return eventService.getAllEventsByClub();
    }
    @PostMapping
    public WrapperResponse addEvent(@RequestBody EventRequest addEventRequest){
        return eventService.addEvent(addEventRequest);
    }
    @DeleteMapping("/{event_id}")
    public WrapperResponse deleteEvent(@PathVariable("event_id") Long eventId){
        return eventService.deleteEvent(eventId);
    }
    @PutMapping
    public WrapperResponse editEvent(@RequestParam Long eventId,@RequestBody EventRequest editEventRequest){
        return eventService.editEvent(eventId,editEventRequest);
    }
}
