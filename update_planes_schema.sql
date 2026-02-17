-- 1. Agregar columnas para descuentos y ofertas a la tabla plan_suscripcion
ALTER TABLE plan_suscripcion
ADD COLUMN descuento DECIMAL(5, 2) DEFAULT 0.00, -- Porcentaje de descuento (ej. 20.00 para 20%)
ADD COLUMN oferta_activa BOOLEAN DEFAULT FALSE;  -- Si la oferta está activa o no

-- 2. Crear tabla para los beneficios (descripciones como lista)
-- PostgreSQL Syntax: SERIAL for auto-increment, no ENGINE=InnoDB
CREATE TABLE plan_beneficio (
    id_beneficio SERIAL PRIMARY KEY,
    id_plan BIGINT NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    FOREIGN KEY (id_plan) REFERENCES plan_suscripcion(id_plan) ON DELETE CASCADE
);
