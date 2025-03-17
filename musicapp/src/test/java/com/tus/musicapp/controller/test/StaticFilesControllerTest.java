package com.tus.musicapp.controller.test;

import com.tus.musicapp.controller.StaticFilesController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StaticFilesControllerTest {

    @InjectMocks
    private StaticFilesController staticFilesController;

    private File mockFolder;
    private File mockImage;

    @BeforeEach
    void setUp() throws IOException {
        mockFolder = mock(File.class);
        mockImage = mock(File.class);
    }

    @Test
    void getCarouselImages_ShouldReturnImageList_WhenDirectoryExists() throws IOException {
        try (MockedStatic<ClassPathResource> classPathResourceMock = mockStatic(ClassPathResource.class)) {
            ClassPathResource mockResource = mock(ClassPathResource.class);
            classPathResourceMock.when(() -> new ClassPathResource("static/assets/images/carousel/")).thenReturn(mockResource);
            when(mockResource.getFile()).thenReturn(mockFolder);
            when(mockFolder.exists()).thenReturn(true);
            when(mockFolder.isDirectory()).thenReturn(true);
            when(mockFolder.listFiles()).thenReturn(new File[]{mockImage});
            when(mockImage.isFile()).thenReturn(true);
            when(mockImage.getName()).thenReturn("test.jpg");

            List<String> images = staticFilesController.getCarouselImages();

            assertNotNull(images);
            assertFalse(images.isEmpty());
            assertEquals(1, images.size());
            assertEquals("/assets/images/carousel/test.jpg", images.get(0));
        }
    }

    @Test
    void getCarouselImages_ShouldReturnEmptyList_WhenDirectoryDoesNotExist() throws IOException {
        try (MockedStatic<ClassPathResource> classPathResourceMock = mockStatic(ClassPathResource.class)) {
            ClassPathResource mockResource = mock(ClassPathResource.class);
            classPathResourceMock.when(() -> new ClassPathResource("static/assets/images/carousel/")).thenReturn(mockResource);
            when(mockResource.getFile()).thenReturn(mockFolder);
            when(mockFolder.exists()).thenReturn(false);
            
            List<String> images = staticFilesController.getCarouselImages();
            
            assertNotNull(images);
            assertTrue(images.isEmpty());
        }
    }
}
