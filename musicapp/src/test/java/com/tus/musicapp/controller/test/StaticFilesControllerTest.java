package com.tus.musicapp.controller.test;

import com.tus.musicapp.controller.StaticFilesController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
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
		staticFilesController = new StaticFilesController();
	}

	@Test
	void getCarouselImages_ShouldReturnImageList_WhenDirectoryExists() throws IOException {
		ClassPathResource resource = new ClassPathResource("static/assets/images/carousel/");
		File folder = resource.getFile(); // Ensure the directory exists in test resources

		assertTrue(folder.exists());
		assertTrue(folder.isDirectory());

		List<String> images = staticFilesController.getCarouselImages();

		assertNotNull(images);
		assertFalse(images.isEmpty());
		assertEquals("/assets/images/carousel/image1.jpg", images.get(0));
		assertEquals("/assets/images/carousel/image10.jpg", images.get(1));
	}

}
