import {
  Button,
  Checkbox,
  FormControl,
  FormErrorMessage,
  FormLabel,
  HStack,
  Input,
  InputGroup,
  InputRightElement,
  NumberInput,
  NumberInputField,
  Radio,
  RadioGroup,
  VStack,
} from '@chakra-ui/react';
import { Field, Form, Formik } from 'formik';
import React from 'react';

function Settings() {
  const validate = values => {
    const errors = {};

    // repeatingLoglines validation
    if (
      isNaN(values.repeatingLoglines) ||
      Number(values.repeatingLoglines) < 0 ||
      Number(values.repeatingLoglines) > 100
    ) {
      errors.repeatingLoglines = 'Must be between 0% and 100%';
    }

    // mode validation
    if (values.mode === 'Stream') {
      // stream mode validation
      if (values.stream.streamAddress === '') {
        errors.stream = {};
        errors.stream.streamAddress = 'Required';
      }
    } else if (values.mode === 'Batch') {
      // batch mode validation
      if (isNaN(values.batch.numberOfLogs)) {
        errors.batch = {};
        errors.batch.numberOfLogs = 'Must be a number';
      } else if (Number(values.batch.numberOfLogs) < 1) {
        errors.batch = {};
        errors.batch.numberOfLogs = 'Must be more than 0';
      } else if (Number(values.batch.numberOfLogs) > 1000000000) {
        errors.batch = {};
        errors.batch.numberOfLogs =
          'Must be a number equal or below 1,000,000,000';
      }
    } else {
      errors.mode = 'Invalid mode selected';
    }

    return errors;
  };

  return (
    <Formik
      initialValues={{
        repeatingLoglines: 0,
        fields: {
          includeTimeStamp: true,
          includeProcessingTime: true,
          includeCurrentUserID: true,
          includeBusinessGUID: true,
          includePathToFile: true,
          includeFileSHA256: true,
          includeDisposition: true,
        },
        malware: {
          includeTrojan: false,
          includeAdware: false,
          includeRansom: false,
        },
        mode: 'Stream',
        stream: {
          saveLogs: false,
          streamAddress: '',
        },
        batch: {
          numberOfLogs: 0,
        },
      }}
      validate={validate}
      onSubmit={(values, actions) => {
        // round numberOfLogs to nearest integer
        values.batch.numberOfLogs = Math.round(values.batch.numberOfLogs);

        // display json output
        setTimeout(() => {
          alert(JSON.stringify(values, null, 2));
          actions.setSubmitting(false);
        }, 1000);
      }}
    >
      {props => (
        <Form>
          <VStack spacing="1.5em" align="flex-start">
            <Field name="repeatingLoglines">
              {({ field, form }) => (
                <FormControl isInvalid={form.errors.repeatingLoglines}>
                  <FormLabel>Repeating Loglines</FormLabel>
                  <InputGroup maxW="6em">
                    <NumberInput
                      min={0}
                      max={100}
                      onChange={val => form.setFieldValue(field.name, val)}
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
                    {form.errors.repeatingLoglines}
                  </FormErrorMessage>
                </FormControl>
              )}
            </Field>
            <FormControl>
              <FormLabel>Include Fields:</FormLabel>
              <VStack spacing="0.75em" align="flex-start">
                <Field
                  as={Checkbox}
                  name="fields.includeTimeStamp"
                  defaultChecked
                >
                  Time stamp
                </Field>
                <Field
                  as={Checkbox}
                  name="fields.includeProcessingTime"
                  defaultChecked
                >
                  Processing time
                </Field>
                <Field
                  as={Checkbox}
                  name="fields.includeCurrentUserID"
                  defaultChecked
                >
                  Current user ID
                </Field>
                <Field
                  as={Checkbox}
                  name="fields.includeBusinessGUID"
                  defaultChecked
                >
                  Business GUID
                </Field>
                <Field
                  as={Checkbox}
                  name="fields.includePathToFile"
                  defaultChecked
                >
                  Path to file
                </Field>
                <Field
                  as={Checkbox}
                  name="fields.includeFileSHA256"
                  defaultChecked
                >
                  File SHA256
                </Field>
                <Field
                  as={Checkbox}
                  name="fields.includeDisposition"
                  defaultChecked
                >
                  Disposition
                </Field>
              </VStack>
            </FormControl>
            <FormControl>
              <FormLabel>Include Malware:</FormLabel>
              <VStack spacing="0.75em" align="flex-start">
                <Field as={Checkbox} name="malware.includeTrojan">
                  Trojan
                </Field>
                <Field as={Checkbox} name="malware.includeAdware">
                  Adware
                </Field>
                <Field as={Checkbox} name="malware.includeRansom">
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
                <Field name="stream.streamAddress">
                  {({ field, meta }) => (
                    <FormControl
                      isRequired
                      isInvalid={meta.touched && meta.error}
                    >
                      <FormLabel>Stream Address</FormLabel>
                      <Input
                        {...field}
                        aria-required={false}
                        placeholder="Stream address"
                      />
                      <FormErrorMessage>{meta.error}</FormErrorMessage>
                    </FormControl>
                  )}
                </Field>
                <Field as={Checkbox} name="stream.saveLogs">
                  Save logs
                </Field>
              </VStack>
            )}
            {props.values.mode === 'Batch' && (
              <div>
                <Field name="batch.numberOfLogs">
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
              </div>
            )}
            <Button
              mt={4}
              colorScheme="teal"
              isLoading={props.isSubmitting}
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
