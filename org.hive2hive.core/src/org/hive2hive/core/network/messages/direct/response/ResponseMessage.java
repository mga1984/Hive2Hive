package org.hive2hive.core.network.messages.direct.response;

import java.io.Serializable;

import net.tomp2p.peers.PeerAddress;

import org.hive2hive.core.log.H2HLogger;
import org.hive2hive.core.log.H2HLoggerFactory;
import org.hive2hive.core.network.NetworkManager;
import org.hive2hive.core.network.messages.AcceptanceReply;
import org.hive2hive.core.network.messages.direct.BaseDirectMessage;
import org.hive2hive.core.network.messages.request.callback.ICallBackHandler;

public class ResponseMessage extends BaseDirectMessage {

	private static final H2HLogger logger = H2HLoggerFactory
			.getLogger(ResponseMessage.class);

	private static final long serialVersionUID = -4182581031050888858L;

	private final PeerAddress targetAddress;
	private final Serializable content;

	public ResponseMessage(String messageID, String targetKey,
			PeerAddress requesterAddress, Serializable someContent) {
		super(messageID, targetKey, requesterAddress, false);
		targetAddress = requesterAddress;
		content = someContent;
	}

	@Override
	public void run() {
		ICallBackHandler handler = networkManager.getMessageManager()
				.getCallBackHandlers().remove(getMessageID());
		if (handler != null) {
			handler.handleReturnMessage(this);
		} else {
			logger.warn(String
					.format("No call back handler for this message! currentNodeID='%s', AsyncReturnMessage='%s'",
							networkManager.getNodeId(), this));
		}
	}

	@Override
	public AcceptanceReply accept() {
		if (networkManager.getMessageManager().getCallBackHandlers()
				.get(getMessageID()) != null) {
			return AcceptanceReply.OK;
		}
		return AcceptanceReply.NO_CALLBACK_HANDLER_FOR_THIS_MESSAGE;
	}

	public PeerAddress getTargetAddress() {
		return targetAddress;
	}

	public Object getContent() {
		return content;
	}

	@Override
	public void handleSendingFailure(AcceptanceReply reply,
			NetworkManager aNetworkManager) {
		if (AcceptanceReply.NO_CALLBACK_HANDLER_FOR_THIS_MESSAGE == reply) {
			logger.warn(String
					.format("Receiving node has no callback handler for this message. message id = %s",
							getMessageID()));
		} else {
			super.handleSendingFailure(reply, aNetworkManager);
		}
	}

}