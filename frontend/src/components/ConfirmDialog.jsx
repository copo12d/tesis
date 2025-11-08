import React, { useState, useCallback, useEffect } from "react";
import {
  Dialog,
  Portal,
  Button,
  CloseButton,
  HStack,
  Text,
  Box,
  Icon,
} from "@chakra-ui/react";
import { LiaExclamationTriangleSolid } from "react-icons/lia";

export function ConfirmDialog({
  trigger,
  title = "Confirmar",
  description,
  confirmText = "Aceptar",
  cancelText = "Cancelar",
  onConfirm,
  confirmColorPalette = "teal",
  loading = false,
  icon = <LiaExclamationTriangleSolid />,
  hideCloseButton = false,

  descriptionColor = "gray.700",
  iconColor,
  contentColorPalette = "teal",
  contentBg = "white",
  headerBg = "teal.700",
  headerBorderColor = "teal.600",
  footerBg = "white",
  backdropBg = "blackAlpha.400",
  titleColor = "WhiteAlpha.900",
  cancelVariant = "outline",
  cancelColorPalette = "gray",
  cancelTextColor,
  confirmVariant = "solid",

  // new: controlled open
  isOpen: isOpenProp,
  onOpenChange: onOpenChangeProp,
  showCancelButton = false,
}) {
  const [internalOpen, setInternalOpen] = useState(false);

  // if parent controls open, sync
  useEffect(() => {
    if (typeof isOpenProp === "boolean") {
      setInternalOpen(isOpenProp);
    }
  }, [isOpenProp]);

  const handleOpenChange = useCallback(
    (nextOpen) => {
      if (typeof onOpenChangeProp === "function") {
        onOpenChangeProp(nextOpen);
      } else {
        setInternalOpen(nextOpen);
      }
    },
    [onOpenChangeProp]
  );

  const handleConfirm = useCallback(async () => {
    if (onConfirm) {
      const r = onConfirm();
      if (r instanceof Promise) {
        try {
          await r;
        } catch {
          /* silencioso */
        }
      }
    }
    // close only if parent didn't control open
    if (typeof onOpenChangeProp !== "function") setInternalOpen(false);
    else onOpenChangeProp(false);
  }, [onConfirm, onOpenChangeProp]);

  const resolvedIconColor = iconColor || `${confirmColorPalette}.500`;
  const open = typeof isOpenProp === "boolean" ? isOpenProp : internalOpen;

  return (
    <Dialog.Root open={open} onOpenChange={(d) => handleOpenChange(d)}>
      {trigger ? (
        <Dialog.Trigger asChild>
          <Box as="span" onClick={() => handleOpenChange(true)}>
            {trigger}
          </Box>
        </Dialog.Trigger>
      ) : null}

      <Portal>
        <Dialog.Backdrop bg={backdropBg} />
        <Dialog.Positioner>
          <Dialog.Content
            maxW="400px"
            w="90%"
            borderRadius="md"
            shadow="lg"
            colorPalette={contentColorPalette}
            bg={contentBg}
            m={4}
          >
            <Dialog.Header
              p={4}
              borderBottomWidth="1px"
              borderColor={headerBorderColor}
              bg={headerBg}
            >
              <HStack gap="2">
                {icon && (
                  <Icon color={resolvedIconColor} boxSize="5" as={() => icon} />
                )}
                <Dialog.Title color={titleColor}>{title}</Dialog.Title>
              </HStack>
            </Dialog.Header>

            <Dialog.Body p={4} bg={contentBg}>
              {description && (
                <Text fontSize="sm" color={descriptionColor}>
                  {description}
                </Text>
              )}
            </Dialog.Body>

            <Dialog.Footer p={4} bg={footerBg} borderTopWidth="0">
              <HStack gap="2" justify="flex-end" w="100%">
                {showCancelButton && (
                  <Dialog.ActionTrigger asChild>
                    <Button
                      variant={cancelVariant}
                      size="sm"
                      colorPalette={cancelColorPalette}
                      disabled={loading}
                      color={cancelTextColor}
                      px={2}
                      onClick={() => handleOpenChange(false)}
                    >
                      {cancelText}
                    </Button>
                  </Dialog.ActionTrigger>
                )}
                <Button
                  size="sm"
                  variant={confirmVariant}
                  colorPalette={confirmColorPalette}
                  onClick={handleConfirm}
                  disabled={loading}
                  loading={loading}
                  loadingText="Procesando..."
                  spinnerPlacement="end"
                  px={2}
                >
                  {confirmText}
                </Button>
              </HStack>
            </Dialog.Footer>

            {!hideCloseButton && (
              <Dialog.CloseTrigger asChild>
                <CloseButton size="sm" />
              </Dialog.CloseTrigger>
            )}
          </Dialog.Content>
        </Dialog.Positioner>
      </Portal>
    </Dialog.Root>
  );
}
