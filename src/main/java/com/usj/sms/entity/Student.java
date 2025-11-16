package com.usj.sms.entity; 

import jakarta.persistence.*; 
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data; 
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "students") 
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 
    
    @NotBlank(message = "Name is mandatory") 
    private String name;
    
    @NotBlank(message = "Email is mandatory") 
    @Email(message = "Email should be a valid format")
    private String email;
    
    @NotBlank(message = "Course is mandatory")
    private String course;
    
    @NotNull(message = "Age is mandatory")
    @Min(value = 18, message = "Student must be at least 18 years old")
    private Integer age;
}
