package items;

import org.apollo.game.event.EventSubscriber;
import org.apollo.game.event.annotate.SubscribesTo;
import org.apollo.game.interact.ItemActionEvent;
import org.apollo.game.model.Interfaces;
import org.apollo.game.model.Player;
import org.apollo.game.model.inter.InterfaceSet;
import org.apollo.game.model.inter.bank.BankConstants;
import org.apollo.game.model.inter.bank.BankDepositEnterAmountListener;
import org.apollo.game.model.inter.bank.BankUtils;

@SubscribesTo(ItemActionEvent.class)
@SuppressWarnings("all")
public class DepositBank implements EventSubscriber<ItemActionEvent> {
  public void subscribe(final ItemActionEvent event) {
    Interfaces.InterfaceOption _option = event.getOption();
    int amount = Interfaces.InterfaceOption.optionToAmount(_option);
    if ((amount == (-1))) {
      Player _player = event.getPlayer();
      InterfaceSet _interfaceSet = _player.getInterfaceSet();
      Player _player_1 = event.getPlayer();
      int _slot = event.getSlot();
      int _id = event.getId();
      BankDepositEnterAmountListener _bankDepositEnterAmountListener = new BankDepositEnterAmountListener(_player_1, _slot, _id);
      _interfaceSet.openEnterAmountDialog(_bankDepositEnterAmountListener);
    } else {
      Player _player_2 = event.getPlayer();
      int _slot_1 = event.getSlot();
      int _id_1 = event.getId();
      boolean _deposit = BankUtils.deposit(_player_2, _slot_1, _id_1, amount);
      boolean _not = (!_deposit);
      if (_not) {
        return;
      }
    }
  }
  
  public boolean test(final ItemActionEvent event) {
    boolean _and = false;
    int _interfaceId = event.getInterfaceId();
    boolean _equals = (_interfaceId == BankConstants.SIDEBAR_INVENTORY_ID);
    if (!_equals) {
      _and = false;
    } else {
      Player _player = event.getPlayer();
      InterfaceSet _interfaceSet = _player.getInterfaceSet();
      boolean _contains = _interfaceSet.contains(BankConstants.BANK_WINDOW_ID, BankConstants.SIDEBAR_ID);
      _and = _contains;
    }
    return _and;
  }
}
