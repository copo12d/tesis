import {
  Box,
  Button,
  Stack,
  Text,
  VStack,
  Spinner,
} from "@chakra-ui/react";
import { useEffect, useState, useRef, useCallback } from "react";
import {
  MapContainer,
  TileLayer,
  Marker,
  Popup,
  useMapEvents,
  useMap,
} from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import { toast } from "react-hot-toast";
import { SettingsAPI } from "../api/api.settings";

import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";

// Configurar íconos de Leaflet
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x,
  iconUrl: markerIcon,
  shadowUrl: markerShadow,
});

function CenterMapButton({ center, zoom }) {
  const map = useMap();
  return (
    <Box position="absolute" top={2} right={2} zIndex={1000}>
      <Button size="sm" onClick={() => map.setView(center, zoom)}>
        Centrar ubicación
      </Button>
    </Box>
  );
}

function LocationSelector({ onClick }) {
  useMapEvents({
    click(e) {
      onClick(e.latlng);
    },
  });
  return null;
}

export default function UbicationSettingDialog() {
  const [form, setForm] = useState(null);
  const [loading, setLoading] = useState(false);
  const markerRef = useRef(null);
  const hasCentered = useRef(false);

  const markerPosition = form ? [form.latitude, form.longitude] : null;

  useEffect(() => {
    SettingsAPI.getUbication()
      .then((res) => {
        const data = res.data.data || {};
        setForm({
          latitude: data.latitude,
          longitude: data.longitude,
          mapZoom: data.mapZoom ?? 20,
        });
        hasCentered.current = true;
      })
      .catch((error) => {
        console.error("Error loading ubication data:", error);
        toast.error("Error al cargar los datos de ubicación");
      });
  }, []);

  const handleMapClick = useCallback((latlng) => {
    setForm((prev) => ({
      ...prev,
      latitude: latlng.lat,
      longitude: latlng.lng,
    }));
    setTimeout(() => {
      markerRef.current?.openPopup();
    }, 100);
  }, []);

  const handleSubmit = async () => {
    setLoading(true);
    try {
      await SettingsAPI.updateUbication(form);
      toast.success("Ubicación institucional actualizada.");
    } catch {
      toast.error("No se pudo actualizar la ubicación.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Stack
      spacing={0}
      borderRadius="md"
      boxShadow="md"
      borderWidth={1}
      borderColor="green.600"
      bg="whiteAlpha.900"
      maxW="6xl"
      mx="auto"
    >
      {/* Encabezado */}
      <Box
        bg="green.600"
        color="white"
        px={6}
        py={4}
        borderTopRadius="md"
        borderBottom="1px solid"
        borderColor="green.700"
      >
        <Text fontSize="xl" fontWeight="bold">
          Ubicación Institucional
        </Text>
      </Box>

      {/* Contenido */}
      <Box px={6} py={6}>
        <Stack spacing={4}>
          <VStack align="start" spacing={2} w="full">
            <Text fontSize="sm" fontWeight="medium" color="gray.700">
              Haz clic en el mapa para seleccionar la ubicación
            </Text>

            <Box width="100%" height="400px">
              {form ? (
                <MapContainer
                  center={hasCentered.current ? markerPosition : undefined}
                  zoom={form.mapZoom}
                  scrollWheelZoom={false}
                  style={{
                    width: "100%",
                    height: "100%",
                    position: "relative",
                  }}
                >
                  <CenterMapButton center={markerPosition} zoom={form.mapZoom} />
                  <TileLayer
                    attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                    url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
                  />
                  <LocationSelector onClick={handleMapClick} />
                  <Marker
                    position={markerPosition}
                    draggable={true}
                    ref={markerRef}
                    eventHandlers={{
                      dragend: (e) => {
                        const latlng = e.target.getLatLng();
                        setForm((prev) => ({
                          ...prev,
                          latitude: latlng.lat,
                          longitude: latlng.lng,
                        }));
                        setTimeout(() => {
                          markerRef.current?.openPopup();
                        }, 100);
                      },
                    }}
                  >
                    <Popup
                      autoClose={false}
                      closeOnClick={false}
                      closeButton={false}
                      keepInView={true}
                    >
                      <Text fontSize="sm">
                        Latitud: {form.latitude?.toFixed(6)}
                        <br />
                        Longitud: {form.longitude?.toFixed(6)}
                      </Text>
                    </Popup>
                  </Marker>
                </MapContainer>
              ) : (
                <Box
                  width="100%"
                  height="100%"
                  display="flex"
                  alignItems="center"
                  justifyContent="center"
                >
                  <Spinner size="lg" color="green.500" />
                </Box>
              )}
            </Box>

            {!form?.latitude || !form?.longitude ? (
              <Text fontSize="xs" color="red.500">
                Debes seleccionar una ubicación en el mapa
              </Text>
            ) : (
              <Text fontSize="xs" color="gray.500">
                Puedes mover el marcador o hacer clic en el mapa para ajustar la ubicación.
              </Text>
            )}
          </VStack>

          <Button
            colorScheme="green"
            bg="green.600"
            color="white"
            _hover={{ bg: "green.700" }}
            _active={{ bg: "green.800" }}
            onClick={handleSubmit}
            isLoading={loading}
            loadingText="Guardando..."
            boxShadow="sm"
            isDisabled={!form}
          >
            Guardar ubicación
          </Button>
        </Stack>
      </Box>
    </Stack>
  );
}
