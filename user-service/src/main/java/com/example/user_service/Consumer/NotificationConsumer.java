package com.example.user_service.Consumer;


import com.example.user_service.dto.NotificationMessage;
import com.example.user_service.entity.Employee;
import com.example.user_service.entity.Role;
import com.example.user_service.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationConsumer {

    private final EmployeeRepository employeeRepository;
    private static final Logger LOGGER= LoggerFactory.getLogger(NotificationConsumer.class);
    public NotificationConsumer(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @RabbitListener(queues = "${rabbitmq.admin.queue}")
    public void handleDeviceAdded(NotificationMessage message){

        List<Employee> admins = employeeRepository.findByRole(Role.ADMIN);
        if(admins.isEmpty()){
            System.out.println("No admins are there to notify");
        }
        admins.forEach(admin -> {
            System.out.println("Notifying ADMIN: " + admin.getEmail()
                    + " | " + message.getMessage());
            // later: send email / push / in-app
        });
    }

    @RabbitListener(queues = "${rabbitmq.employee.queue}")
    public void handleDeviceAssigned(NotificationMessage message) {
        Employee employee = employeeRepository.findById(message.getRecipientId())
                .orElseThrow(() -> new RuntimeException(
                        "Employee not found: " + message.getRecipientId()));

        System.out.println("Notifying EMPLOYEE: " + employee.getEmail()
                + " | " + message.getMessage());
        // later: send email / push / in-app
    }
}
