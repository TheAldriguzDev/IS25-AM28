package it.polimi.ingsw.is25am28.Model.ActionJSON.State;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import it.polimi.ingsw.is25am28.Model.ActionJSON.BoardJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.CardStateJSON;
import it.polimi.ingsw.is25am28.Model.ActionJSON.PlayerJSON;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CardRoundDTO extends StateDTO {
    private int round;
    private BoardJSON board;
    private Map<String, PlayerJSON> playersInfo;    // Already in ClientModel
    private CardStateJSON cardInfo;
    private boolean isCardNew;

    // TODO: Understand if other attributes are needed

    public CardRoundDTO() {}

    public CardRoundDTO(
            @JsonProperty("round") int round,
            @JsonProperty("board") BoardJSON board,
            @JsonProperty("playersInfo") Map<String, PlayerJSON> playersInfo,
            @JsonProperty("cardInfo") CardStateJSON cardInfo,
            @JsonProperty("isCardNew") boolean isCardNew
    ) {
        this.round = round;
        this.board = board;
        this.playersInfo = playersInfo;
        this.cardInfo = cardInfo;
        this.isCardNew = isCardNew;
    }

    @JsonGetter("round")
    public int getRound() {
        return round;
    }

    @JsonSetter("round")
    public CardRoundDTO setRound(int round) {
        this.round = round;
        return this;
    }

    @JsonGetter("board")
    public BoardJSON getBoard() {
        return board;
    }

    @JsonSetter("board")
    public void setBoard(BoardJSON board) {
        this.board = board;
    }

    @JsonGetter("playersInfo")
    public Map<String, PlayerJSON> getPlayersInfo() {
        return playersInfo;
    }

    @JsonSetter("playersInfo")
    public void setPlayersInfo(Map<String, PlayerJSON> playersInfo) {
        this.playersInfo = playersInfo;
    }

    @JsonGetter("cardInfo")
    public CardStateJSON getCardInfo() {
        return cardInfo;
    }

    @JsonSetter("cardInfo")
    public CardRoundDTO setCardInfo(CardStateJSON cardInfo) {
        this.cardInfo = cardInfo;
        return this;
    }

    @JsonGetter("isCardNew")
    public boolean isCardNew() {
        return isCardNew;
    }

    @JsonSetter("isCardNew")
    public CardRoundDTO setCardNew(boolean isCardNew) {
        this.isCardNew = isCardNew;
        return this;
    }

    @Override
    public void accept(StateVisitor visitor) throws Exception {
        visitor.visit(this);
    }
}
