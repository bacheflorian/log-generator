import { AddIcon, DownloadIcon, SmallCloseIcon } from '@chakra-ui/icons';
import {
  Button,
  Divider,
  Flex,
  FormControl,
  FormErrorMessage,
  FormLabel,
  HStack,
  IconButton,
  Input,
  InputGroup,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalHeader,
  ModalOverlay,
  NumberInput,
  NumberInputField,
  Text,
  Textarea,
  VStack,
  Wrap,
  WrapItem,
} from '@chakra-ui/react';
import { isNaN } from 'formik';
import React, { useCallback, useEffect, useRef, useState } from 'react';
import { v4 as uuidv4 } from 'uuid';

function CustomLogsModal({
  isOpen,
  onClose,
  field,
  form,
  meta,
  defaultCustomLog,
}) {
  const [customLogsText, setCustomLogsText] = useState('');
  const [customLogsTextError, setCustomLogsTextError] = useState(null);
  const fileRef = useRef(null); // ref for the file input

  function parseKeyValue(key, value, emptyValue) {
    if (value === '') return emptyValue;
    else if (!['pathToFile', 'fileSHA256'].includes(key))
      return isNaN(Number(value)) ? value : Number(value);
    else return value;
  }

  const updateCustomLogs = useCallback(() => {
    function replacer(key, value) {
      if (key === 'id') return undefined;
      return parseKeyValue(key, value, undefined);
    }

    if (field.value.length === 0) {
      setCustomLogsText('');
    } else {
      setCustomLogsText(JSON.stringify(field.value, replacer, 2));
    }
    setCustomLogsTextError(null);

    // reset fileRef
    if (fileRef.current) {
      fileRef.current.value = null;
    }
  }, [field.value]);

  useEffect(() => {
    updateCustomLogs();
  }, [updateCustomLogs]);

  function onCloseModal() {
    onClose();
    updateCustomLogs();
  }

  function loadCustomLogs() {
    let error = null;

    // reset fileRef
    if (fileRef.current) {
      fileRef.current.value = null;
    }

    try {
      // parse text
      const customLogs = JSON.parse(customLogsText);

      // validate object is an array
      if (!Array.isArray(customLogs)) throw Error('JSON must be an array');

      // parse for each log
      customLogs.forEach(function (customLog, index) {
        // reset log
        customLogs[index] = {};

        // set frequency to default or number entered
        if (isNaN(Number(customLog.frequency))) {
          customLogs[index].frequency = defaultCustomLog.frequency;
        } else {
          customLogs[index].frequency = customLog.frequency;
        }

        // add all given fields to standard log fields
        customLogs[index].fields = {
          ...defaultCustomLog.fields,
          ...customLog.fields,
        };

        // set random id to custom log
        customLogs[index].id = uuidv4();
      });

      // set customLogs field
      form.setFieldValue(field.name, customLogs);

      console.log(JSON.stringify(customLogs, null, 2));
    } catch (err) {
      error = err.message;
    } finally {
      setCustomLogsTextError(error);
    }
  }

  const handleFileUpload = event => {
    // validate file it given
    if (!event.target.files[0]) {
      return;
    }
    const selectedFile = event.target.files[0];

    const allowedTypes = ['text/plain', 'application/json'];

    // validte file is in allowedTypes
    if (allowedTypes.includes(selectedFile.type)) {
      // read file contents
      const reader = new FileReader();
      reader.onload = event => {
        const content = event.target.result;
        console.log(content);

        // try to format json, otherwise set to text
        let data;
        try {
          data = JSON.stringify(JSON.parse(content), null, 2);
          console.log(data);
        } catch (error) {
          data = content;
        }

        setCustomLogsText(data);
      };
      reader.readAsText(selectedFile);
    } else {
      // reset fileRef
      if (fileRef.current) {
        fileRef.current.value = null;
      }
      alert('Please upload a .json or .txt file.');
    }
  };

  const handleDownload = () => {
    // try to format json, otherwise set to text
    let data;
    try {
      data = JSON.stringify(JSON.parse(customLogsText), null, 2);
      console.log(data);
    } catch (error) {
      data = customLogsText;
    }

    const blob = new Blob([data], { type: 'application/json' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `customLogs${new Date()
      .toLocaleString()
      .replace(/[/\\?%*:|"<>]/g, '-')
      .replace(/, /g, '_')}.json`;
    link.click();
    URL.revokeObjectURL(url);
  };

  return (
    <Modal isOpen={isOpen} onClose={onCloseModal} size="6xl" closeOnEsc={false}>
      <ModalOverlay />
      <ModalContent>
        <ModalHeader>Custom Logs</ModalHeader>
        <ModalCloseButton />
        <ModalBody>
          <VStack align="start">
            {field.value.map((log, index) => (
              <VStack key={log.id}>
                <HStack>
                  <Wrap spacingX="1em" spacingY="0.5em" gap="1em" pt="0.5em">
                    <WrapItem>
                      <HStack>
                        <Text>frequency:</Text>
                        <FormControl isInvalid={meta.error}>
                          <InputGroup maxW="8em">
                            <NumberInput
                              min={0}
                              max={1}
                              value={log.frequency}
                              onChange={val =>
                                form.setFieldValue(
                                  `customLogs.${index}.frequency`,
                                  val
                                )
                              }
                            >
                              <NumberInputField
                                h="2em"
                                w="6em"
                                placeholder="0"
                              />
                            </NumberInput>
                          </InputGroup>
                        </FormControl>
                      </HStack>
                    </WrapItem>
                    {Object.keys(log.fields).map(keyName => (
                      <WrapItem key={`${log.id}.${keyName}`}>
                        <HStack>
                          <Text>{keyName}:</Text>
                          <FormControl>
                            <Input
                              defaultValue={log.fields[keyName]}
                              //change values onBlur to improve rendering performance
                              onBlur={event =>
                                form.setFieldValue(
                                  `customLogs.${index}.fields.${keyName}`,
                                  parseKeyValue(keyName, event.target.value, '')
                                )
                              }
                              placeholder="random"
                              h="2em"
                              w="8em"
                            />
                          </FormControl>
                        </HStack>
                      </WrapItem>
                    ))}
                  </Wrap>
                  <IconButton
                    icon={<SmallCloseIcon />}
                    variant="ghost"
                    onClick={() => {
                      form.setFieldValue(
                        field.name,
                        field.value.filter((_, i) => i !== index)
                      );
                    }}
                  />
                </HStack>
                <Divider pt="0.5em" />
              </VStack>
            ))}
            <FormErrorMessage>{meta.error}</FormErrorMessage>
            <IconButton
              icon={<AddIcon />}
              size="sm"
              onClick={() =>
                form.setFieldValue(field.name, [
                  ...field.value,
                  {
                    ...defaultCustomLog,
                    id: uuidv4(),
                  },
                ])
              }
            />
            <FormControl pt="0.75em" w="85%" isInvalid={customLogsTextError}>
              <FormLabel>Load custom loglines</FormLabel>
              <Textarea
                placeholder="Insert custom logs in JSON format, must be an array"
                h="13em"
                value={customLogsText}
                onChange={e => setCustomLogsText(e.target.value)}
              />
              <Flex
                h="2.5em"
                w="full"
                mt="0.2em"
                align="flex-start"
                justifyContent="space-between"
              >
                <Input
                  variant="filled"
                  type="file"
                  accept=".json,.txt"
                  ref={fileRef}
                  onChange={handleFileUpload}
                  isInvalid={false}
                  value={null}
                  w="15em"
                  pt="0.2em"
                />
                <IconButton
                  aria-label="Download"
                  variant="ghost"
                  onClick={handleDownload}
                  icon={<DownloadIcon />}
                />
              </Flex>
              <FormErrorMessage>{customLogsTextError}</FormErrorMessage>
            </FormControl>

            <HStack spacing="0.5em" pt="0.3em">
              <Button colorScheme="green" onClick={loadCustomLogs}>
                Load
              </Button>

              <Button variant="ghost" onClick={updateCustomLogs}>
                Reset Text
              </Button>
            </HStack>
          </VStack>
        </ModalBody>

        <ModalFooter>
          <Button
            variant="ghost"
            mr={3}
            onClick={() => form.setFieldValue(field.name, [])}
          >
            Reset
          </Button>
          <Button colorScheme="blue" onClick={onClose}>
            Close
          </Button>
        </ModalFooter>
      </ModalContent>
    </Modal>
  );
}

export default CustomLogsModal;
