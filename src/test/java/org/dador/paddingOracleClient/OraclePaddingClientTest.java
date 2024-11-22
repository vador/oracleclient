package org.dador.paddingOracleClient;

import static org.dador.paddingOracleClient.HexConverters.*;
import static org.dador.paddingOracleClient.OraclePaddingClient.*;
import static org.junit.Assert.*;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the OraclePadding Decryption client
 * Created by dame on 18/10/2016.
 */
public class OraclePaddingClientTest {
    static final String ENCRYPTED_MESSAGE = "5ca00ff4c878d61e1edbf1700618fb287c21578c0580965dad57f70636ea402fa0017c4acc82717730565174e2e3f713d3921bab07cba15f3197b87976525ce4";

    @Test
    public void should_buildGuessForPositionLastBit_return_() throws Exception {
        OraclePaddingClient opc = new OraclePaddingClient();
        byte[] cypher = toByteArrayFromHex("02020202020202020202020202020202");
        byte[] decoded = toByteArrayFromHex("00000000000000000000000000000000");
        byte guess = 0x25;
        int position = 15; // padding shoud be                   "00000000000000000000000000000001"
        byte[] expected = toByteArrayFromHex("02020202020202020202020202020226");
        assertArrayEquals(expected, opc.buildGuessForPosition(cypher, decoded, position, guess));
    }

    @Test
    public void should_buildGuessForPosition_return_() throws Exception {
        OraclePaddingClient opc = new OraclePaddingClient();
        byte[] cypher = toByteArrayFromHex("02020202020202020202020202020202");
        byte[] decoded = toByteArrayFromHex("00000000000000000000000000656565");
        byte guess = 1;
        int position = 12; // padding shoud be                   "00000000000000000000000004040404"
        byte[] expected = toByteArrayFromHex("02020202020202020202020207636363");
        assertArrayEquals(expected, opc.buildGuessForPosition(cypher, decoded, position, guess));

    }

    @Test
    public void should_buildGuessForPosition_with_empty_valsreturn_() throws Exception {
        OraclePaddingClient opc = new OraclePaddingClient();

        byte[] cypher = new byte[BLOCK_SIZE];
        byte[] decoded = new byte[BLOCK_SIZE];
        byte guess = 2;
        int position = 15; // padding shoud be                   "00000000000000000000000004040404"
        byte[] expected = toByteArrayFromHex("00000000000000000000000000000003");
        assertArrayEquals(expected, opc.buildGuessForPosition(cypher, decoded, position, guess));

    }


    @Test
    public void should_getPaddingArray__return_valid_padding_for_02() throws Exception {
        OraclePaddingClient opc = new OraclePaddingClient();

        byte[] expected = new byte[BLOCK_SIZE];
        expected[expected.length - 1] = (byte) 2;
        expected[expected.length - 2] = (byte) 2;
        byte[] result = opc.buildPaddingArray(2);

        assertArrayEquals(expected, result);
    }

    @Test
    public void should_getPaddingArray_return_full_block_for_padding_32() throws Exception {
        OraclePaddingClient opc = new OraclePaddingClient();

        byte[] expected = HexConverters.toByteArrayFromHex("10101010101010101010101010101010");
        byte[] result = opc.buildPaddingArray(16);
        assertArrayEquals(expected, result);
    }

    @Test
    public void should_split_32ByteMsg_into_2_blocs() throws Exception {
        OraclePaddingClient opc = new OraclePaddingClient();

        String message = "0123456789ABCDEF0123456789ABCDEFFEDCBA9876543210FEDCBA9876543210";
        byte[] hexMessage = toByteArrayFromHex(message);
        byte[][] result = opc.splitMessageIntoBlocks(hexMessage);
        assertEquals(2, result.length);
        assertArrayEquals(toByteArrayFromHex("0123456789ABCDEF0123456789ABCDEF"), result[0]);
        assertArrayEquals(toByteArrayFromHex("FEDCBA9876543210FEDCBA9876543210"), result[1]);

    }

    @Test
    public void should_return_PaddingLength_ForLastBlock() throws Exception {
        OraclePaddingClient opc = new OraclePaddingClient();
        PaddingOracleQuery poq = new PaddingOracleQuery();

        String message = ENCRYPTED_MESSAGE;
        byte[] hexMessage = toByteArrayFromHex(message);
        byte[][] splitMSG = opc.splitMessageIntoBlocks(hexMessage);
        int result = opc.getPaddingLengthForLastBlock(poq, splitMSG[2], splitMSG[3]);
        Assert.assertEquals(6, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_return_exception_for_middle_block() throws Exception {
        OraclePaddingClient opc = new OraclePaddingClient();
        PaddingOracleQuery poq = new PaddingOracleQuery();

        String message = ENCRYPTED_MESSAGE;
        byte[] hexMessage = toByteArrayFromHex(message);
        byte[][] splitMSG = opc.splitMessageIntoBlocks(hexMessage);
        int result = opc.getPaddingLengthForLastBlock(poq, splitMSG[1], splitMSG[2]);
        Assert.assertEquals(6, result);
    }
}