package com.marcouberti.caregivers.webservice.parser;

import com.marcouberti.caregivers.db.entity.CaregiverEntity;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class CaregiverServiceParserUnitTest {

    @Test
    public void parseEmptyList() {
        String json = "{\"results\":[]}";
        List<CaregiverEntity> list = CaregiversServiceParser.parse(json);

        assertNotNull(list);
        assertEquals(0, list.size());
    }

    @Test
    public void parseList() {
        String json = "{\"results\":[{\"gender\":\"male\",\"name\":{\"title\":\"mr\",\"first\":\"lucas\",\"last\":\"harcourt\"},\"location\":{\"street\":\"2090 lake of bays road\",\"city\":\"alma\",\"state\":\"saskatchewan\",\"postcode\":\"N3N 4Y8\",\"coordinates\":{\"latitude\":\"-68.3011\",\"longitude\":\"-67.3615\"},\"timezone\":{\"offset\":\"-7:00\",\"description\":\"Mountain Time (US & Canada)\"}},\"email\":\"lucas.harcourt@example.com\",\"login\":{\"uuid\":\"963390eb-0ae2-404a-9b2f-27665a4e3a3a\",\"username\":\"blackzebra231\",\"password\":\"polska\",\"salt\":\"aP5idsOM\",\"md5\":\"3c62e79fc6683df3159ede9056483ee0\",\"sha1\":\"64e079e9af25aa20c547c2081795ff2e815ba644\",\"sha256\":\"3e25b01634f9da82e29dd75fdf431d00481f8abfcbce3eaa91885b365bfaac78\"},\"dob\":{\"date\":\"1968-07-21T11:00:22Z\",\"age\":50},\"registered\":{\"date\":\"2008-05-21T11:06:00Z\",\"age\":10},\"phone\":\"362-826-2634\",\"cell\":\"222-385-2642\",\"id\":{\"name\":\"\",\"value\":null},\"picture\":{\"large\":\"https://randomuser.me/api/portraits/men/41.jpg\",\"medium\":\"https://randomuser.me/api/portraits/med/men/41.jpg\",\"thumbnail\":\"https://randomuser.me/api/portraits/thumb/men/41.jpg\"},\"nat\":\"CA\"},{\"gender\":\"female\",\"name\":{\"title\":\"miss\",\"first\":\"charlotte\",\"last\":\"price\"},\"location\":{\"street\":\"4532 king street\",\"city\":\"hereford\",\"state\":\"berkshire\",\"postcode\":\"CO24 8ZZ\",\"coordinates\":{\"latitude\":\"-31.9206\",\"longitude\":\"-0.1906\"},\"timezone\":{\"offset\":\"+7:00\",\"description\":\"Bangkok, Hanoi, Jakarta\"}},\"email\":\"charlotte.price@example.com\",\"login\":{\"uuid\":\"3345d94a-7386-487f-99b3-b1c74601e6d7\",\"username\":\"redkoala722\",\"password\":\"bigone\",\"salt\":\"4yZDdO65\",\"md5\":\"8fc7b52c1c69e0d3efbebd4bac2df511\",\"sha1\":\"6a5bc5f1b72592cfbb59745feb44bd76285be030\",\"sha256\":\"7ba99248e6f21b53d0c112114074b8af818ac545dd1cd662d6cc9ace8ab87345\"},\"dob\":{\"date\":\"1989-04-22T19:52:56Z\",\"age\":29},\"registered\":{\"date\":\"2013-10-02T17:40:49Z\",\"age\":5},\"phone\":\"0111469 553 2429\",\"cell\":\"0799-385-046\",\"id\":{\"name\":\"NINO\",\"value\":\"NC 16 31 38 F\"},\"picture\":{\"large\":\"https://randomuser.me/api/portraits/women/88.jpg\",\"medium\":\"https://randomuser.me/api/portraits/med/women/88.jpg\",\"thumbnail\":\"https://randomuser.me/api/portraits/thumb/women/88.jpg\"},\"nat\":\"GB\"}],\"info\":{\"seed\":\"empatica\",\"results\":2,\"page\":1,\"version\":\"1.2\"}}";
        List<CaregiverEntity> list = CaregiversServiceParser.parse(json);

        assertNotNull(list);
        assertEquals(2, list.size());
    }

    @Test
    public void parseWithException() {
        String json = "{\"results\":[{\"gender\":\"male\",\"invalid_name\":{\"title\":\"mr\",\"first\":\"lucas\",\"last\":\"harcourt\"},\"location\":{\"street\":\"2090 lake of bays road\",\"city\":\"alma\",\"state\":\"saskatchewan\",\"postcode\":\"N3N 4Y8\",\"coordinates\":{\"latitude\":\"-68.3011\",\"longitude\":\"-67.3615\"},\"timezone\":{\"offset\":\"-7:00\",\"description\":\"Mountain Time (US & Canada)\"}},\"email\":\"lucas.harcourt@example.com\",\"login\":{\"uuid\":\"963390eb-0ae2-404a-9b2f-27665a4e3a3a\",\"username\":\"blackzebra231\",\"password\":\"polska\",\"salt\":\"aP5idsOM\",\"md5\":\"3c62e79fc6683df3159ede9056483ee0\",\"sha1\":\"64e079e9af25aa20c547c2081795ff2e815ba644\",\"sha256\":\"3e25b01634f9da82e29dd75fdf431d00481f8abfcbce3eaa91885b365bfaac78\"},\"dob\":{\"date\":\"1968-07-21T11:00:22Z\",\"age\":50},\"registered\":{\"date\":\"2008-05-21T11:06:00Z\",\"age\":10},\"phone\":\"362-826-2634\",\"cell\":\"222-385-2642\",\"id\":{\"name\":\"\",\"value\":null},\"picture\":{\"large\":\"https://randomuser.me/api/portraits/men/41.jpg\",\"medium\":\"https://randomuser.me/api/portraits/med/men/41.jpg\",\"thumbnail\":\"https://randomuser.me/api/portraits/thumb/men/41.jpg\"},\"nat\":\"CA\"},{\"gender\":\"female\",\"name\":{\"title\":\"miss\",\"first\":\"charlotte\",\"last\":\"price\"},\"location\":{\"street\":\"4532 king street\",\"city\":\"hereford\",\"state\":\"berkshire\",\"postcode\":\"CO24 8ZZ\",\"coordinates\":{\"latitude\":\"-31.9206\",\"longitude\":\"-0.1906\"},\"timezone\":{\"offset\":\"+7:00\",\"description\":\"Bangkok, Hanoi, Jakarta\"}},\"email\":\"charlotte.price@example.com\",\"login\":{\"uuid\":\"3345d94a-7386-487f-99b3-b1c74601e6d7\",\"username\":\"redkoala722\",\"password\":\"bigone\",\"salt\":\"4yZDdO65\",\"md5\":\"8fc7b52c1c69e0d3efbebd4bac2df511\",\"sha1\":\"6a5bc5f1b72592cfbb59745feb44bd76285be030\",\"sha256\":\"7ba99248e6f21b53d0c112114074b8af818ac545dd1cd662d6cc9ace8ab87345\"},\"dob\":{\"date\":\"1989-04-22T19:52:56Z\",\"age\":29},\"registered\":{\"date\":\"2013-10-02T17:40:49Z\",\"age\":5},\"phone\":\"0111469 553 2429\",\"cell\":\"0799-385-046\",\"id\":{\"name\":\"NINO\",\"value\":\"NC 16 31 38 F\"},\"picture\":{\"large\":\"https://randomuser.me/api/portraits/women/88.jpg\",\"medium\":\"https://randomuser.me/api/portraits/med/women/88.jpg\",\"thumbnail\":\"https://randomuser.me/api/portraits/thumb/women/88.jpg\"},\"nat\":\"GB\"}],\"info\":{\"seed\":\"empatica\",\"results\":2,\"page\":1,\"version\":\"1.2\"}}";

        List<CaregiverEntity> list = CaregiversServiceParser.parse(json);
        assertNotNull(list);
        assertNotEquals(2, list.size());
    }
}
