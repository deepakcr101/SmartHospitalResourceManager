package com.hospital.resource_manager.service;

import com.hospital.resource_manager.domain.Doctor;

import com.hospital.resource_manager.domain.Procedure;
import com.hospital.resource_manager.domain.Room;
import com.hospital.resource_manager.repository.DoctorRepository;
import com.hospital.resource_manager.repository.EquipmentRepository;
import com.hospital.resource_manager.repository.PatientRepository;
import com.hospital.resource_manager.repository.ProcedureRepository;
import com.hospital.resource_manager.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DataUpdationServiceTest {

    @Mock
    private PatientRepository patientRepo;
    @Mock
    private ProcedureRepository procedureRepo;
    @Mock
    private DoctorRepository doctorRepo;
    @Mock
    private RoomRepository roomRepo;
    @Mock
    private EquipmentRepository equipmentRepo;

    @InjectMocks
    private DataUpdationService dataUpdationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddDoctor() {
        Doctor doctor = new Doctor();
        doctor.setName("Dr. Test");
        when(doctorRepo.save(any(Doctor.class))).thenReturn(doctor);

        Doctor saved = dataUpdationService.addDoctor(doctor);

        assertNotNull(saved);
        assertEquals("Dr. Test", saved.getName());
        verify(doctorRepo).save(doctor);
    }

    @Test
    void testDeleteDoctor() {
        Long id = 1L;
        doNothing().when(doctorRepo).deleteById(id);

        dataUpdationService.deleteDoctor(id);

        verify(doctorRepo).deleteById(id);
    }

    @Test
    void testUpdateRoomOccupied() {
        Long id = 1L;
        Room room = new Room();
        room.setId(id);
        room.setOccupied(false);

        when(roomRepo.findById(id)).thenReturn(Optional.of(room));
        when(roomRepo.save(any(Room.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Room> updated = dataUpdationService.updateRoomOccupied(id, true);

        assertTrue(updated.isPresent());
        assertTrue(updated.get().isOccupied());
        verify(roomRepo).save(room);
    }

    @Test
    void testAddProcedure() {
        Procedure procedure = new Procedure();
        procedure.setName("Test Proc");
        when(procedureRepo.save(any(Procedure.class))).thenReturn(procedure);

        Procedure saved = dataUpdationService.addProcedure(procedure);

        assertEquals("Test Proc", saved.getName());
        verify(procedureRepo).save(procedure);
    }
}
