package oth.ics.wtp.tweetgobackend.services;

import org.junit.jupiter.api.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.*;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileStorageServiceTest {

    private FileStorageService service;
    private Path uploadsLocation;

    @BeforeEach
    void setUp() throws Exception {
        service = new FileStorageService();

        Field locField = FileStorageService.class
                .getDeclaredField("fileStorageLocation");
        locField.setAccessible(true);
        uploadsLocation = (Path) locField.get(service);

        if (Files.exists(uploadsLocation)) {
            Files.walk(uploadsLocation)
                    .filter(path -> !path.equals(uploadsLocation))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        if (Files.exists(uploadsLocation)) {
            Files.walk(uploadsLocation)
                    .filter(path -> !path.equals(uploadsLocation))
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @Test
    void uploadsDirectoryCreated() {
        assertTrue(
                Files.exists(uploadsLocation) && Files.isDirectory(uploadsLocation),
                "uploads directory should be created by the constructor"
        );
    }

    @Test
    void storeFile_savesTxtAndReturnsName() throws IOException {
        byte[] data = "hello world".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "greeting.txt",
                "text/plain",
                data
        );

        String storedName = service.storeFile(file);

        assertTrue(
                storedName.endsWith(".txt"),
                "Returned filename should preserve the .txt extension"
        );

        Path written = uploadsLocation.resolve(storedName);
        assertTrue(Files.exists(written), "File should be written to uploads directory");
        assertArrayEquals(
                data,
                Files.readAllBytes(written),
                "Contents of the stored file must match the input stream"
        );
    }

    @Test
    void storeFile_noExtensionOnOriginal() throws IOException {
        byte[] data = "no extension".getBytes();
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "noext",
                "text/plain",
                data
        );

        String storedName = service.storeFile(file);
        assertFalse(
                storedName.contains("."),
                "When original filename has no dot, stored name must not contain one"
        );

        Path written = uploadsLocation.resolve(storedName);
        assertTrue(Files.exists(written), "File should still be created even without extension");
    }

    @Test
    void storeFile_ioExceptionDuringCopy_throwsRuntime() throws IOException {
        MultipartFile bad = mock(MultipartFile.class);
        when(bad.getOriginalFilename()).thenReturn("whatever.txt");
        when(bad.getInputStream()).thenThrow(new IOException("disk full"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> service.storeFile(bad),
                "IOException during copy should bubble up as RuntimeException"
        );
        assertTrue(
                ex.getMessage().contains("Could not store file"),
                "Error message should indicate storage failure"
        );
    }
}
