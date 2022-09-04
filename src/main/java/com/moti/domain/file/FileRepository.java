package com.moti.domain.file;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;

@Repository
@RequiredArgsConstructor
public class FileRepository {

    private final EntityManager em;

    public File findById(Long fileId) {
        return em.find(File.class, fileId);
    }
}
