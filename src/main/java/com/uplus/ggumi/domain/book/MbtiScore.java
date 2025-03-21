package com.uplus.ggumi.domain.book;

import jakarta.persistence.Embeddable;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor
public class MbtiScore {

    private double EI;
    private double SN;
    private double FT;
    private double PJ;

    @Builder
    private MbtiScore(double EI, double SN, double FT, double PJ) {
        validateScore(EI, SN, FT, PJ);
        this.EI = EI;
        this.SN = SN;
        this.FT = FT;
        this.PJ = PJ;
    }

    private void validateScore(double... scores) {
        for (double score : scores) {
            if (score < 0 || score > 100) {
                throw new IllegalArgumentException("MBTI 점수는 0 ~ 100 점 사이여야 합니다.");
            }
        }
    }

    public MbtiScore calculateNewScoreForLike(MbtiScore otherScore, double learningRate) {
        return MbtiScore.builder()
                .EI(calculateNewValueForLike(this.EI, otherScore.getEI(), learningRate))
                .SN(calculateNewValueForLike(this.SN, otherScore.getSN(), learningRate))
                .FT(calculateNewValueForLike(this.FT, otherScore.getFT(), learningRate))
                .PJ(calculateNewValueForLike(this.PJ, otherScore.getPJ(), learningRate))
                .build();
    }

    private double calculateNewValueForLike(double current, double target, double learningRate) {
        return Math.max(0, Math.min(100, current + (learningRate * (target - current))));
    }
}
