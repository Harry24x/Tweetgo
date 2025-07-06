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
        // 1) Instantiate, which will create the "uploads" dir
        service = new FileStorageService();

        // 2) Reflectively grab its private fileStorageLocation
        Field locField = FileStorageService.class
                .getDeclaredField("fileStorageLocation");
        locField.setAccessible(true);
        uploadsLocation = (Path) locField.get(service);

        // 3) Clean out ONLY the *contents* of uploadsLocation
        if (Files.exists(uploadsLocation)) {
            Files.walk(uploadsLocation)
                    .filter(path -> !path.equals(uploadsLocation))   // <-- skip the root
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up again to leave no temp files behind
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
                "file",              // form field name
                "greeting.txt",      // original filename
                "text/plain",        // content type
                data
        );

        String storedName = service.storeFile(file);

        // it should preserve the ".txt" extension
        assertTrue(
                storedName.endsWith(".txt"),
                "Returned filename should preserve the .txt extension"
        );

        // and the file really must exist under uploads/
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
                "noext",            // no "." in the original name
                "text/plain",
                data
        );

        String storedName = service.storeFile(file);

        // since original had no dot, service must not introduce any
        assertFalse(
                storedName.contains("."),
                "When original filename has no dot, stored name must not contain one"
        );

        Path written = uploadsLocation.resolve(storedName);
        assertTrue(Files.exists(written), "File should still be created even without extension");
    }

    @Test
    void storeFile_ioExceptionDuringCopy_throwsRuntime() throws IOException {
        // mock a file that throws on getInputStream()
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
