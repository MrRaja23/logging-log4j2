/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.logging.log4j.perf.nogc;

import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.pattern.LogEventPatternConverter;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MultiformatMessage;

/**
 * Returns the event's rendered message in a StringBuilder.
 */
//@Plugin(name = "MessagePatternConverter", category = PatternConverter.CATEGORY)
//@ConverterKeys({ "m", "msg", "message" })
public final class NoGcMessagePatternConverter extends LogEventPatternConverter {

    private final String[] formats;
    private final Configuration config;

    /**
     * Private constructor.
     * @param options options, may be null.
     */
    private NoGcMessagePatternConverter(final Configuration config, final String[] options) {
        super("Message", "message");
        formats = options;
        this.config = config;
    }

    /**
     * Obtains an instance of pattern converter.
     *
     * @param config The Configuration.
     * @param options options, may be null.
     * @return instance of pattern converter.
     */
    public static NoGcMessagePatternConverter newInstance(final Configuration config, final String[] options) {
        return new NoGcMessagePatternConverter(config, options);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void format(final LogEvent event, final StringBuilder toAppendTo) {
        final Message msg = event.getMessage();
        if (msg != null) {
            String result;
            if (msg instanceof NoGcMessage) {
                toAppendTo.append(((NoGcMessage) msg).get());
                return;
            }
            if (msg instanceof MultiformatMessage) {
                result = ((MultiformatMessage) msg).getFormattedMessage(formats);
            } else {
                result = msg.getFormattedMessage();
            }
            if (result != null) {
                toAppendTo.append(config != null && result.contains("${") ?
                        config.getStrSubstitutor().replace(event, result) : result);
            } else {
                toAppendTo.append("null");
            }
        }
    }
}
