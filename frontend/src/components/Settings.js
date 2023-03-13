import {
  Box,
  Button,
  Checkbox,
  FormControl,
  FormErrorMessage,
  FormHelperText,
  FormLabel,
  HStack,
  Input,
  InputGroup,
  InputRightElement,
  NumberInput,
  NumberInputField,
  Radio,
  RadioGroup,
  Text,
  VStack,
} from '@chakra-ui/react';
import { Field, Form, Formik } from 'formik';
import React from 'react';

function Settings({ jobID, setJobID, setBatchMode, setBatchSize }) {
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

    return errors;
  };
  const FieldSetting = ({ name, fieldName }) => {
    return (
      <HStack justifyContent="space-between" w="85%">
        <Field
          as={Checkbox}
          name={'fieldSettings.include' + fieldName}
          defaultChecked
        >
          {name}
        </Field>
        <Box w="11em">
          <Field
            name={
              'fieldSettings.fieldValues.' +
              fieldName.charAt(0).toLowerCase() +
              fieldName.slice(1)
            }
          >
            {({ field, meta }) => (
              <Input {...field} placeholder="randomly generated" h="2.25em" />
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
          includeTimeStamp: true,
          includeProcessingTime: true,
          includeCurrentUserID: true,
          includeBusinessGUID: true,
          includePathToFile: true,
          includeFileSHA256: true,
          includeDisposition: true,
          fieldValues: {
            timeStamp: '',
            processingTime: '',
            currentUserID: '',
            businessGUID: '',
            pathToFile: '',
            fileSHA256: '',
            disposition: '',
          },
        },
        malwareSettings: {
          includeTrojan: false,
          includeAdware: false,
          includeRansom: false,
        },
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

        // Create a copy of values
        const body = JSON.parse(JSON.stringify(values));

        // split fieldValues
        Object.keys(body.fieldSettings.fieldValues).forEach(function (key) {
          const val = body.fieldSettings.fieldValues[key];

          if (val === '') {
            // if no value is entered, send empty array
            body.fieldSettings.fieldValues[key] = [];
          } else if (val === ',') {
            // if only a comma entered, send array with a single empty string
            body.fieldSettings.fieldValues[key] = [''];
          } else {
            // split string by comma
            body.fieldSettings.fieldValues[key] = val.split(',');
          }
        });

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
          <VStack spacing="1.5em" align="flex-start">
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
            <FormControl w="30em">
              <FormLabel mb="0">Field Settings:</FormLabel>
              <FormHelperText mt="0" pb="0.75em">
                Select fields to include.
                <br />
                All fields will be randomly generated unless values are
                specified. <br />
                To provide multiple values separate them with a comma.
              </FormHelperText>
              <HStack w="60%" justify="space-between" pb="0.75em">
                <Text fontWeight="500">Fields</Text>
                <Text fontWeight="500">Values</Text>
              </HStack>
              <VStack spacing="0.5em" align="flex-start">
                <FieldSetting name="Time stamp" fieldName="TimeStamp" />
                <FieldSetting
                  name="Processing time"
                  fieldName="ProcessingTime"
                />
                <FieldSetting
                  name="Current user ID"
                  fieldName="CurrentUserID"
                />
                <FieldSetting name="Business GUID" fieldName="BusinessGUID" />
                <FieldSetting name="Path to file" fieldName="PathToFile" />
                <FieldSetting name="File SHA256" fieldName="FileSHA256" />
                <FieldSetting name="Disposition" fieldName="Disposition" />
              </VStack>
            </FormControl>
            <FormControl>
              <FormLabel>Include Malware:</FormLabel>
              <VStack spacing="0.75em" align="flex-start">
                <Field as={Checkbox} name="malwareSettings.includeTrojan">
                  Trojan
                </Field>
                <Field as={Checkbox} name="malwareSettings.includeAdware">
                  Adware
                </Field>
                <Field as={Checkbox} name="malwareSettings.includeRansom">
                  Ransom
                </Field>
              </VStack>
            </FormControl>
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
