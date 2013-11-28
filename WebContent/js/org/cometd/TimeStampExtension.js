/**
 * Dual licensed under the Apache License 2.0 and the MIT license.
 * $Revision: 1.1 $ $Date: 2013/09/09 13:43:42 $
 */

if (typeof dojo!="undefined") dojo.provide("org.cometd.TimeStampExtension");

/**
 * The timestamp extension adds the optional timestamp field to all outgoing messages.
 */

org.cometd.TimeStampExtension = function()
{
    this.outgoing = function(message)
    {
        message.timestamp = new Date().toUTCString();
        return message;
    }
}
