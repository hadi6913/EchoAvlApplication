package com.ar.echoafcavlapplication.Models;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MessageProtocol {
    private static Logger log = LoggerFactory.getLogger(MessageProtocol.class);
    public static final String SetLineBusIdCommand = "set_trip";
    public static final String SetDateCommand = "set_date";
    public static final String ChangeCommand = "change";
    public static final String AckCommand = "ack";
    public static final String NackCommand = "nack";
    public static final String KeepAliveCommand = "kp";
    public static final String NotifyCommand = "notify";
    public static final String CheckUpdateCommand = "check_update";
    public static final String CRCCheckCommand = "cc";
    public static final String CRCCheckReponseCommand = "crc_res";

    public static final String SendStatusCommand = "send_status";
    public static final String SendGeneralSystemStatusCommand = "send_od";
    public static final String OKCommand = "ok";
    public static final String BadRequestCommand = "bad_request";

    public static final String DirectionAttribute = "direction";
    public static final String IpAddressAttribute = "ip_address";
    public static final String PortNumberAttribute = "port_number";
    public static final String EquipmentIdAttribute = "equipment_id";
    public static final String BusCodeAttribute = "bc";
    public static final String GateEngageTypeAttribute = "engage_type";
    public static final String EquipmentTypeAttribute = "equipment_type";
    public static final String StationIdAttribute = "station_id";
    public static final String FileCountAttribute = "file_count";
    public static final String ReaderTypeAttribute = "reader_type";
    public static final String SamAvailableAttribute = "sam_available";
    public static final String BaseAddressAttribute = "base_address";
    public static final String GateAlarmTypeAttribute = "alarm_type";
    public static final String FileNameAttribute = "fn";
    public static final String FileCRCAttribute = "crc";
    public static final String CRCResultAttribute = "res";

    public static final String SendRegisterCommand = "reg";
    public static final String SendRegisterStatusCommand = "check_reg";
    public static final String AllReadyRegisteredCommand = "reged";
    public static final String AllReadyRegisterCommand = "reged";
    public static final String WrongLicenceCommand = "wrong_lic";
    public static final String dcRegisterValue = "dc_reg_sts";
    public static final String bvOneRegisterValue = "bv1_reg_sts";
    public static final String bvTwoRegisterValue = "bv2_reg_sts";
    public static final String InvalidRegisterCommand = "inv_reg";

    public static final String dcClientDate = "dccd";
    public static final String dcSoftwareVersion = "dcsv";
    public static final String dcCurrentParamVersion = "dccpv";
    public static final String dcNextParamVersion = "dcnpv";
    public static final String dcParamLoadDate = "dcpld";
    public static final String dcBlackListVersion = "dcblv";
    public static final String dcOperatorVersion = "dcov";
    public static final String dcClientEnabled = "dcce";
    public static final String dcValidationCount = "dcvc";
    public static final String gpsModuleStatus = "gpss";
    public static final String dcRemainingFileSequence = "dcrfs";
    public static final String dcClientTripCode = "dcctc";
    public static final String dcEquipmentCode = "dcec";
    public static final String dcErrorCode = "dcerc";

    public static final String v1ClientDate = "v1cd";
    public static final String v1SoftwareVersion = "v1sv";
    public static final String v1CurrentParamVersion = "v1cpv";
    public static final String v1NextParamVersion = "v1npv";
    public static final String v1ParamLoadDate = "v1pld";
    public static final String v1BlackListVersion = "v1blv";
    public static final String v1OperatorVersion = "v1ov";
    public static final String v1ClientEnabled = "v1ce";
    public static final String v1ValidationCount = "v1vc";
    public static final String v1RemainingFileSequence = "v1rfs";
    public static final String v1ClientTripCode = "v1ctc";
    public static final String v1EquipmentCode = "v1ec";
    public static final String v1ErrorCode = "v1erc";

    public static final String v2ClientDate = "v2cd";
    public static final String v2SoftwareVersion = "v2sv";
    public static final String v2CurrentParamVersion = "v2cpv";
    public static final String v2NextParamVersion = "v2npv";
    public static final String v2ParamLoadDate = "v2pld";
    public static final String v2BlackListVersion = "v2blv";
    public static final String v2OperatorVersion = "v2ov";
    public static final String v2ClientEnabled = "v2ce";
    public static final String v2ValidationCount = "v2vc";
    public static final String v2RemainingFileSequence = "v2rfs";
    public static final String v2ClientTripCode = "v2ctc";
    public static final String v2EquipmentCode = "v2ec";
    public static final String v2ErrorCode = "v2erc";

    public static final String shiftOpenStatus = "sos";
    public static final String busCode = "bc";
    public static final String lineCode = "lc";

    public static final String totalInternalStorage = "tis";
    public static final String totalPhoneStorage = "tps";
    public static final String imei = "imei";
    public static final String availableInternalStorage = "ais";
    public static final String availablePhoneStorage = "aps";

    public static final String DC_ID_TAG = "dc_id";
    public static final String DC_LIC_TAG = "dc_lic";
    public static final String BV_ONE_LIC_TAG = "bv1_lic";
    public static final String BV_TWO_LIC_TAG = "bv2_lic";
    public static final String BV_THREE_LIC_TAG = "bv3_lic";

    public static final String AttributeDelimeter = ";";
    public static final String KeyValueDelimeter = "=";
    public static final String EndChar = "#";
    public static final String ValueDelimiter = ",";
    public static final String MultipleValueDelimiter = "-";

    public static final String DateFormat = "yyyy/MM/dd HH:mm:ss";

    private String commandStr;
    protected String type = "";
    private Boolean isStateFull;
    private SimpleDateFormat sdf = new SimpleDateFormat(DateFormat);
    private Date dateTime;
    private Map<String, String> params = new HashMap<String, String>();
    private static final List<String> commands = Arrays.asList(SetDateCommand, AckCommand, KeepAliveCommand);

    public MessageProtocol(String cmdStr) {
        try {
            cmdStr = cmdStr.toLowerCase();
            this.commandStr = cmdStr;
            if (!cmdStr.endsWith(EndChar)) {
                throw new Exception("End of command not found.");
            } else {
                cmdStr = cmdStr.substring(0, cmdStr.length() - 1);
            }
            if (cmdStr.endsWith(AttributeDelimeter)) {
                cmdStr = cmdStr.substring(0, cmdStr.length() - 1);
            }
            String[] cmdParts = cmdStr.split(AttributeDelimeter);
            type = cmdParts[0].toLowerCase();
            dateTime = sdf.parse(cmdParts[1]);
            //dateTime = Date.parse(cmdParts[1], DateFormat, CultureInfo.InvariantCulture);
            int paramCount = Integer.parseInt(cmdParts[2]);
            if (cmdParts.length - 3 != paramCount) {
                throw new Exception("Command length is not valid.");
            } else {
                for (int i = 0; i < paramCount; i++) {
                    String[] tmp = cmdParts[3 + i].split(KeyValueDelimeter);
                    this.params.put(tmp[0].toLowerCase().trim(), tmp.length > 1 ? tmp[1].toLowerCase().trim() : "");
                }
            }
        } catch (Exception ex) {
            log.error("MessageProtocol() --> MessageProtocol():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public MessageProtocol(String type, Date date, Map<String, String> parameters) {
        try {
            this.type = type;
            this.dateTime = date;
            this.params = parameters;
            GenerateCommandString();
        } catch (Exception ex) {
            log.error("MessageProtocol() --> MessageProtocol():" + ex.getMessage());
            log.error(ex.toString());
        }
    }

    public String ToTheString() {
        return commandStr;
    }

    public static boolean IsAcceptable(MessageProtocol message) {
        try {
            return (commands.contains(message.type));
        } catch (Exception ex) {
            log.error("MessageProtocol() --> IsAcceptable():" + ex.getMessage());
            log.error(ex.toString());
            return false;
        }
    }

    private void GenerateCommandString() {
        try {
            StringBuilder builder = new StringBuilder();
            commandStr = "";
            builder.append(type + AttributeDelimeter);
            builder.append(sdf.format(dateTime) + AttributeDelimeter);
            if (params != null) {
                builder.append(params.size() + AttributeDelimeter);
                Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> pairs = (Map.Entry<String, String>) iterator.next();
                    builder.append(pairs.getKey().trim() + KeyValueDelimeter + pairs.getValue().trim() + AttributeDelimeter);
                }
            } else {
                builder.append("0" + AttributeDelimeter);
            }
            builder.append(EndChar);
            commandStr = builder.toString();
        } catch (Exception ex) {
            log.error("MessageProtocol() --> GenerateCommandString():" + ex.getMessage());
            log.error(ex.toString());
            commandStr = "";
        }
    }

    public String getType() {
        return type;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public Date getDateTime() {
        return dateTime;
    }
}



