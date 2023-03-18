import { AddIcon } from '@chakra-ui/icons';
import {
  Box,
  Button,
  Checkbox,
  Divider,
  FormControl,
  FormErrorMessage,
  FormHelperText,
  FormLabel,
  HStack,
  IconButton,
  Input,
  InputGroup,
  InputRightElement,
  Modal,
  ModalBody,
  ModalCloseButton,
  ModalContent,
  ModalFooter,
  ModalHeader,
  ModalOverlay,
  NumberInput,
  NumberInputField,
  Radio,
  RadioGroup,
  Text,
  useDisclosure,
  VStack,
  Wrap,
  WrapItem,
} from '@chakra-ui/react';
import { Field, Form, Formik } from 'formik';
import React from 'react';

const defaultCustomLog = {
  frequency: 0.05,
  fields: {
    timeStamp: '',
    processingTime: '',
    currentUserID: '',
    businessGUID: '',
    pathToFile: '',
    fileSHA256: '',
    disposition: '',
  },
};

function Settings({ jobID, setJobID, setBatchMode, setBatchSize }) {
  const { isOpen, onOpen, onClose } = useDisclosure();

  const validate = values => {
    const errors = {};

    // repeatingLoglinesPercent validation
    if (
      isNaN(values.repeatingLoglinesPercent) ||
      Number(values.repeatingLoglinesPercent) < 0 ||
      Number(values.repeatingLoglinesPercent) > 1
    ) {
      errors.repeatingLoglinesPercent = 'Must be between 0% and 100%';
    }

    // mode validation
    if (values.mode === 'Stream') {
      // stream mode validation
      if (values.streamSettings.streamAddress === '') {
        errors.streamSettings = { streamAddress: 'Required' };
      }
    } else if (values.mode === 'Batch') {
      // batch mode validation
      if (isNaN(values.batchSettings.numberOfLogs)) {
        errors.batchSettings = {
          numberOfLogs: 'Must be a number',
        };
      } else if (Number(values.batchSettings.numberOfLogs) < 1) {
        errors.batchSettings = {
          numberOfLogs: 'Must be more than 0',
        };
      } else if (Number(values.batchSettings.numberOfLogs) > 1000000000) {
        errors.batchSettings = {
          numberOfLogs: 'Must be a number equal or below 1,000,000,000',
        };
      }
    } else {
      errors.mode = 'Invalid mode selected';
    }

    // custom logs validation
    // validate total frequency less than or equal to 1
    let total = 0;
    values.customLogs.forEach(function (customLog) {
      total += Number(customLog.frequency);
    });

    if (total > 1) {
      errors.customLogs =
        'Frequency of all custom logs must be less than or equal to 1';
    }

    return errors;
  };

  const FieldSetting = ({ name, fieldName }) => {
    return (
      <HStack justifyContent="space-between" w="92%">
        <Field
          as={Checkbox}
          name={'fieldSettings.' + fieldName + '.include'}
          defaultChecked
        >
          {name}
        </Field>
        <Box w="11em">
          <Field name={'fieldSettings.' + fieldName + '.values'}>
            {({ field, meta }) => (
              <Input {...field} placeholder="randomly generated" h="2em" />
            )}
          </Field>
        </Box>
      </HStack>
    );
  };

  return (
    <Formik
      initialValues={{
        repeatingLoglinesPercent: 0,
        fieldSettings: {
          timeStamp: { include: true, values: '' },
          processingTime: { include: true, values: '' },
          currentUserID: { include: true, values: '' },
          businessGUID: { include: true, values: '' },
          pathToFile: { include: true, values: '' },
          fileSHA256: { include: true, values: '' },
          disposition: { include: true, values: '' },
        },
        customLogs: [],
        mode: 'Stream',
        streamSettings: {
          streamAddress: '',
          saveLogs: false,
        },
        batchSettings: {
          numberOfLogs: 0,
        },
      }}
      validate={validate}
      onSubmit={(values, actions) => {
        // round numberOfLogs to nearest integer
        values.batchSettings.numberOfLogs = Math.round(
          values.batchSettings.numberOfLogs
        );

        // Process body
        // Create a copy of values
        const body = JSON.parse(JSON.stringify(values));

        // field settings
        // split field values
        Object.keys(body.fieldSettings).forEach(function (key) {
          const val = body.fieldSettings[key].values;

          if (val === '') {
            // if no value is entered, send empty array
            body.fieldSettings[key].values = [];
          } else if (val === ',') {
            // if only a comma entered, send array with a single empty string
            body.fieldSettings[key].values = [''];
          } else {
            // split string by comma
            body.fieldSettings[key].values = val.split(',');
          }
        });

        // custom logs
        // filter empty fields
        body.customLogs.forEach(function (customLog) {
          Object.keys(customLog.fields).forEach(
            key => customLog.fields[key] === '' && delete customLog.fields[key]
          );
        });

        // filter customLogs with no fields provided
        body.customLogs = body.customLogs.filter(
          customLog => Object.keys(customLog.fields).length !== 0
        );

        // log body
        console.log(JSON.stringify(body, null, 2));

        // request address
        let address = process.env.REACT_APP_API_URL + 'generate/';
        if (values.mode === 'Batch') {
          address = address + 'batch';
        } else {
          address = address + 'stream';
        }

        // request options
        const requestOptions = {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(values),
        };

        // send and handle request
        fetch(address, requestOptions)
          .then(async response => {
            if (response.ok) {
              const data = await response.text();

              console.log(data);
              setJobID(data);
              if (values.mode === 'Batch') {
                setBatchSize(values.batchSettings.numberOfLogs);
                setBatchMode(true);
              } else {
                setBatchSize(null);
                setBatchMode(false);
              }
            } else {
              const error = await response.json();

              throw Error(error.message);
            }
          })
          .catch(err => alert(err))
          .finally(() => actions.setSubmitting(false));
      }}
    >
      {props => (
        <Form>
          <VStack spacing="1em" align="flex-start">
            <Field name="repeatingLoglinesPercent">
              {({ field, form }) => (
                <FormControl isInvalid={form.errors.repeatingLoglinesPercent}>
                  <FormLabel>Repeating Loglines</FormLabel>
                  <InputGroup maxW="6em">
                    <NumberInput
                      min={0}
                      max={100}
                      onChange={val =>
                        form.setFieldValue(field.name, val / 100)
                      }
                    >
                      <NumberInputField placeholder="0" />
                      <InputRightElement
                        pointerEvents="none"
                        color="gray.300"
                        fontSize="1.2em"
                        children="%"
                      />
                    </NumberInput>
                  </InputGroup>
                  <FormErrorMessage>
                    {form.errors.repeatingLoglinesPercent}
                  </FormErrorMessage>
                </FormControl>
              )}
            </Field>
            <FormControl w="26em">
              <FormLabel mb="0">Field Settings:</FormLabel>
              <FormHelperText mt="0" pb="0.75em">
                Select fields to include.
                <br />
                All fields will be randomly generated unless values are
                specified. <br />
                To provide multiple values separate them with a comma.
              </FormHelperText>
              <HStack w="63%" justify="space-between" pb="0.75em">
                <Text fontWeight="500" pl="0.25em">
                  Fields
                </Text>
                <Text fontWeight="500">Values</Text>
              </HStack>
              <VStack spacing="0.3em" align="flex-start">
                <FieldSetting name="Time stamp" fieldName="timeStamp" />
                <FieldSetting
                  name="Processing time"
                  fieldName="processingTime"
                />
                <FieldSetting
                  name="Current user ID"
                  fieldName="currentUserID"
                />
                <FieldSetting name="Business GUID" fieldName="businessGUID" />
                <FieldSetting name="Path to file" fieldName="pathToFile" />
                <FieldSetting name="File SHA256" fieldName="fileSHA256" />
                <FieldSetting name="Disposition" fieldName="disposition" />
              </VStack>
            </FormControl>
            <Field name="customLogs">
              {({ field, form, meta }) => (
                <FormControl isInvalid={meta.error}>
                  <FormLabel>Custom Logs:</FormLabel>
                  <VStack align="start">
                    <HStack spacing="1.5em">
                      <Text fontWeight="medium">{field.value.length}</Text>
                      <Button onClick={onOpen} size="sm">
                        Edit
                      </Button>
                    </HStack>
                    {!isOpen && (
                      <FormErrorMessage mt="0">{meta.error}</FormErrorMessage>
                    )}
                  </VStack>
                  <Modal
                    isOpen={isOpen}
                    onClose={onClose}
                    size="6xl"
                    closeOnEsc={false}
                  >
                    <ModalOverlay />
                    <ModalContent>
                      <ModalHeader>Custom Logs</ModalHeader>
                      <ModalCloseButton />
                      <ModalBody>
                        <VStack align="start">
                          {field.value.map((log, index) => (
                            <Wrap
                              spacingX="1em"
                              spacingY="0.5em"
                              key={index}
                              gap="1em"
                              pt="0.5em"
                            >
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
                                <WrapItem key={`${index}.${keyName}`}>
                                  <HStack>
                                    <Text>{keyName}:</Text>
                                    <FormControl>
                                      <Input
                                        defaultValue={log.fields[keyName]}
                                        //change values onBlur to improve rendering performance
                                        onBlur={event =>
                                          form.setFieldValue(
                                            `customLogs.${index}.fields.${keyName}`,
                                            event.target.value
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
                              <Divider pt="0.5em" />
                            </Wrap>
                          ))}
                          <FormErrorMessage>{meta.error}</FormErrorMessage>
                          <IconButton
                            icon={<AddIcon />}
                            size="sm"
                            onClick={() => {
                              form.setFieldValue(field.name, [
                                ...field.value,
                                defaultCustomLog,
                              ]);
                              console.log(field.value);
                            }}
                          />
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
                </FormControl>
              )}
            </Field>
            <Field name="mode">
              {({ field, meta }) => {
                const { onChange, ...rest } = field;
                return (
                  <FormControl id="mode" isInvalid={meta.touched && meta.error}>
                    <FormLabel htmlFor="mode">Select Mode</FormLabel>
                    <RadioGroup {...rest} id="mode">
                      <HStack spacing="24px">
                        <Radio onChange={onChange} value="Stream">
                          Stream
                        </Radio>
                        <Radio onChange={onChange} value="Batch">
                          Batch
                        </Radio>
                      </HStack>
                    </RadioGroup>
                    <FormErrorMessage>{meta.error}</FormErrorMessage>
                  </FormControl>
                );
              }}
            </Field>
            {props.values.mode === 'Stream' && (
              <VStack spacing="1em" align="flex-start">
                <Field name="streamSettings.streamAddress">
                  {({ field, meta }) => (
                    <FormControl
                      isRequired
                      isInvalid={meta.touched && meta.error}
                    >
                      <FormLabel>Stream Address</FormLabel>
                      <Input {...field} placeholder="Stream address" />
                      <FormErrorMessage>{meta.error}</FormErrorMessage>
                    </FormControl>
                  )}
                </Field>
                <Field as={Checkbox} name="streamSettings.saveLogs">
                  Save logs
                </Field>
              </VStack>
            )}
            {props.values.mode === 'Batch' && (
              <Field name="batchSettings.numberOfLogs">
                {({ field, form, meta }) => (
                  <FormControl
                    isRequired
                    isInvalid={meta.touched && meta.error}
                  >
                    <FormLabel>Number of Logs</FormLabel>
                    <InputGroup maxW="10em">
                      <NumberInput
                        min={1}
                        max={1000000000}
                        precision={0}
                        onChange={val => form.setFieldValue(field.name, val)}
                      >
                        <NumberInputField placeholder="1000" />
                      </NumberInput>
                    </InputGroup>
                    <FormErrorMessage>{meta.error}</FormErrorMessage>
                  </FormControl>
                )}
              </Field>
            )}
            <Button
              mt={4}
              colorScheme="teal"
              isLoading={jobID !== null || props.isSubmitting}
              type="submit"
            >
              Start
            </Button>
          </VStack>
        </Form>
      )}
    </Formik>
  );
}

export default Settings;
