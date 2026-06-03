package com.example.user_service.controller;

import com.example.user_service.dto.*;
import com.example.user_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ── Public endpoints ─────────────────────────────────────

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    // ── Protected endpoints ───────────────────────────────────

    @GetMapping("/profile")
    public ResponseEntity<EmployeeResponse> getMyProfile(@RequestHeader("X-Auth-User") String username,
                                                    @RequestHeader("X-Auth-Role") String role) {
        return ResponseEntity.ok(userService.getMyProfile(username,role));
    }

    @PutMapping("/assign")
    public ResponseEntity<?> assigndevices(@RequestBody AssignmentRequest assignmentRequest,
                                           @RequestHeader("X-Auth-User") String username)
    {
        return userService.assigndevices(assignmentRequest,username);
    }

    @PostMapping("/request/raise")
    public ResponseEntity<?> raiseRepairRequest(@RequestBody RepairRequestDTO repairRequestDTO,
                                                @RequestHeader("X-Auth-User") String username){
       return userService.raiseRepairRequest(repairRequestDTO,username);
    }

    @GetMapping("/myRequests")
    public List<ResponseDTO> getRepairRequest(@RequestHeader("X-Auth-User") String username){
        return userService.getRepairRequest(username);
    }


    @GetMapping("/all")
    public ResponseEntity<List<EmployeeResponse>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<EmployeeResponse> updateRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(userService.updateRole(id, body.get("role")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/devices")
    public ResponseEntity<List<?>> getDevices(@RequestHeader("X-Auth-User") String username) {
        return ResponseEntity.ok(userService.getAssignedDevices(username));
    }

    @GetMapping("/{id}/devices")
    public ResponseEntity<List<?>> getDevicesByEmployeeId(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getAssignedDevicesByEmployeeId(id));
    }

    @PutMapping("/repair/acknwoledge/{id}")
    public ResponseEntity<?> acknwoledgeRequest(@RequestHeader("X-Auth-User") String username,
                                                @PathVariable long id){
        return userService.acknwoledgeRequest(username,id);
    }

    @PutMapping("/repair/close/{id}")
    public ResponseEntity<?> closeRequest(@RequestHeader("X-Auth-User") String username,
                                                @PathVariable long id){
        return userService.closeRequest(username,id);
    }


}
