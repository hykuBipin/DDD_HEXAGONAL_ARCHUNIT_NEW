package com.satellite.infrastructure.adapter.out.persistence.entity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Spring Data JPA repository — lives in the infrastructure adapter layer.
 * The domain knows nothing about this interface.
 */
public interface SatelliteJpaRepository extends JpaRepository<SatelliteJpaEntity, UUID> {}
