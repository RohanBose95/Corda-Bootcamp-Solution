package bootcamp;

import examples.ArtContract;
import examples.ArtState;
import net.corda.core.contracts.*;
import net.corda.core.identity.Party;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.List;

import static net.corda.core.contracts.ContractsDSL.requireSingleCommand;

/* Our contract, governing how our state will evolve over time.
 * See src/main/java/examples/ArtContract.java for an example. */
public class TokenContract implements Contract {
    public static String ID = "bootcamp.TokenContract";

    public static class Issue implements CommandData{}

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException
    {
        if(tx.getCommands().size()!=1)
        {
            throw new IllegalArgumentException("Command should be only one");
        }
        Command command = tx.getCommand(0);
        List<PublicKey> requiredSigners = command.getSigners();
        CommandData commandType = command.getValue();

        if(commandType instanceof  Issue)
        {
            //Shape constraints
            if(tx.getInputStates().size() != 0)
                throw new IllegalArgumentException("Number of Input States must be zero");
            if(tx.getOutputStates().size()!=1)
                throw new IllegalArgumentException("Number of Output States must be 1");

            //Content Constraints
            ContractState outputState = tx.getOutput(0);
            if(!(outputState instanceof TokenState))
                throw new IllegalArgumentException("Output of transaction is not of correct type: TokenState");
            TokenState outputToken = (TokenState)outputState;
            if(outputToken.getAmount() <= 0)
                throw new IllegalArgumentException("Amount Cant be negative");
            //Required Signers
            Party issuer = outputToken.getIssuer();
            PublicKey issuerKey = issuer.getOwningKey();
            if(!(requiredSigners.contains(issuerKey) ))
                throw new IllegalArgumentException("Issuer Must be a signer");

        }
        else
        {
            throw new IllegalArgumentException("Command Not Recognised");
        }
    }

}