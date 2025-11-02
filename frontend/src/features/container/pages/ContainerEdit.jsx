import { useEffect, useMemo, useState } from "react";
import { Box, Spinner, Text } from "@chakra-ui/react";
import { useNavigate, useParams } from "react-router-dom";
import { ContainerForm } from "../components/ContainerForm";
import { ContainerAPI } from "../api/container.api";
import { Marker, Popup } from "react-leaflet";
import { useContainerEdit } from "../hooks/useContainerEdit";
import { toast } from "react-hot-toast"; // <-- añadido

export function ContainerEdit() {
  const { id: idParam } = useParams();
  const id = Number(idParam);
  const navigate = useNavigate();

  const [updating, setUpdating] = useState(false);

  const {
    loading,
    container,
    error,
    coords,
    setCoords,
    markerHandlers,
    makeUpdatePayload,
  } = useContainerEdit(id);

  const popup = (
    <Popup>
      Ubicación del contenedor
      <br />
      {coords ? (
        <>
          Lat: {coords[0].toFixed(6)} <br />
          Lng: {coords[1].toFixed(6)}
        </>
      ) : null}
    </Popup>
  );

  return (
    <Box h="100vh" overflowY="auto" bg="gray.50" px={4} py={14}>
      {loading ? (
        <Box display="flex" justifyContent="center" py={10}>
          <Spinner />
        </Box>
      ) : (
        <>
          {error && (
            <Text color="red.500" textAlign="center" mb={4}>
              {error}
            </Text>
          )}

          <ContainerForm
            initialValues={container}
            loading={updating}
            submitText="Actualizar contenedor"
            title="Editar contenedor"
            mapMarker={
              coords ? (
                <Marker position={coords} draggable eventHandlers={markerHandlers}>
                  {popup}
                </Marker>
              ) : null
            }
            onSubmit={async (values) => {
              try {
                setUpdating(true);
                const payload = makeUpdatePayload(values);
                const res = await ContainerAPI.update(id, payload); // <-- capturar respuesta
                if (res !== false) {
                  const msg =
                    res?.data?.message ??
                    res?.message ??
                    "Contenedor actualizado correctamente";
                  toast.success(msg); // <-- toast con mensaje del API
                  navigate("/container/list");
                }
              } finally {
                setUpdating(false);
              }
            }}
          />
        </>
      )}
    </Box>
  );
}