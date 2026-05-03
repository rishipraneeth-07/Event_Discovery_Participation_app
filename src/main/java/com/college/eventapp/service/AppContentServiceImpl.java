package com.college.eventapp.service;

import com.college.eventapp.dto.AppContentResponseDTO;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AppContentServiceImpl implements AppContentService {

    @Override
    public AppContentResponseDTO getAppContent() {
        AppContentResponseDTO dto = new AppContentResponseDTO();
        dto.setAbout("Event Discovery helps students discover campus events, organizers manage submissions, and admins moderate listings.");
        dto.setTermsSummary("Use the app responsibly, provide accurate event information, and follow campus policies and community guidelines.");
        dto.setHelpCenter(Map.of(
                "student", "Browse approved events, manage saved items, update interests, and track your registrations.",
                "organizer", "Create, edit, duplicate, cancel, and monitor your own event listings from the organizer dashboard.",
                "admin", "Review pending events, approve or reject submissions, and monitor platform stats."
        ));
        return dto;
    }
}
