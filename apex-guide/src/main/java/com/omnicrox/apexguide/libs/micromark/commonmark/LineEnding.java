package com.omnicrox.apexguide.libs.micromark.commonmark;

import com.omnicrox.apexguide.libs.micromark.Assert;
import com.omnicrox.apexguide.libs.micromark.CharUtil;
import com.omnicrox.apexguide.libs.micromark.Construct;
import com.omnicrox.apexguide.libs.micromark.State;
import com.omnicrox.apexguide.libs.micromark.TokenizeContext;
import com.omnicrox.apexguide.libs.micromark.Tokenizer;
import com.omnicrox.apexguide.libs.micromark.Types;
import com.omnicrox.apexguide.libs.micromark.factory.FactorySpace;

public final class LineEnding {
    private LineEnding() {
    }

    public static final Construct lineEnding;

    static {
        lineEnding = new Construct();
        lineEnding.name = "lineEnding";
        lineEnding.tokenize = (context, effects, ok, nok) -> new StateMachine(context, effects, ok, nok)::start;
    }

    private static class StateMachine {
        private final TokenizeContext context;
        private final Tokenizer.Effects effects;
        private final State ok;
        private final State nok;

        public StateMachine(TokenizeContext context, Tokenizer.Effects effects, State ok, State nok) {

            this.context = context;
            this.effects = effects;
            this.ok = ok;
            this.nok = nok;
        }

        private State start(int code) {
            Assert.check(CharUtil.markdownLineEnding(code), "expected eol");
            effects.enter(Types.lineEnding);
            effects.consume(code);
            effects.exit(Types.lineEnding);
            return FactorySpace.create(effects, ok, Types.linePrefix);
        }
    }
}
