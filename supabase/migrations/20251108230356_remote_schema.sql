
\restrict nNT6L1tSq76Hwstl4CkBXKqVedxIgtaaAItHMBMProetqdQQGUTfeYzODYxttOL


SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;


COMMENT ON SCHEMA "public" IS 'standard public schema';



CREATE EXTENSION IF NOT EXISTS "pg_graphql" WITH SCHEMA "graphql";






CREATE EXTENSION IF NOT EXISTS "pg_stat_statements" WITH SCHEMA "extensions";






CREATE EXTENSION IF NOT EXISTS "pgcrypto" WITH SCHEMA "extensions";






CREATE EXTENSION IF NOT EXISTS "supabase_vault" WITH SCHEMA "vault";






CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA "extensions";





SET default_tablespace = '';

SET default_table_access_method = "heap";


CREATE TABLE IF NOT EXISTS "public"."chat_participants" (
    "id" "uuid" NOT NULL,
    "joined_at" timestamp(6) without time zone NOT NULL,
    "last_read_at" timestamp(6) without time zone,
    "role" character varying(255) NOT NULL,
    "user_id" "uuid" NOT NULL,
    "chat_id" "uuid" NOT NULL,
    CONSTRAINT "chat_participants_role_check" CHECK ((("role")::"text" = ANY ((ARRAY['OWNER'::character varying, 'ADMIN'::character varying, 'MEMBER'::character varying])::"text"[])))
);


ALTER TABLE "public"."chat_participants" OWNER TO "postgres";


CREATE TABLE IF NOT EXISTS "public"."chats" (
    "id" "uuid" NOT NULL,
    "created_at" timestamp(6) without time zone NOT NULL,
    "created_by" "uuid" NOT NULL,
    "name" character varying(255),
    "type" character varying(255) NOT NULL,
    "updated_at" timestamp(6) without time zone NOT NULL,
    CONSTRAINT "chats_type_check" CHECK ((("type")::"text" = ANY ((ARRAY['DIRECT'::character varying, 'GROUP'::character varying])::"text"[])))
);


ALTER TABLE "public"."chats" OWNER TO "postgres";


CREATE TABLE IF NOT EXISTS "public"."messages" (
    "id" "uuid" NOT NULL,
    "content" "text" NOT NULL,
    "created_at" timestamp(6) without time zone NOT NULL,
    "deleted_at" timestamp(6) without time zone,
    "edited_at" timestamp(6) without time zone,
    "read_at" timestamp(6) without time zone,
    "sender_id" "uuid" NOT NULL,
    "status" character varying(255) NOT NULL,
    "chat_id" "uuid" NOT NULL,
    CONSTRAINT "messages_status_check" CHECK ((("status")::"text" = ANY ((ARRAY['SENT'::character varying, 'DELIVERED'::character varying, 'READ'::character varying])::"text"[])))
);


ALTER TABLE "public"."messages" OWNER TO "postgres";


ALTER TABLE ONLY "public"."chat_participants"
    ADD CONSTRAINT "chat_participants_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."chats"
    ADD CONSTRAINT "chats_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."messages"
    ADD CONSTRAINT "messages_pkey" PRIMARY KEY ("id");



ALTER TABLE ONLY "public"."messages"
    ADD CONSTRAINT "fk64w44ngcpqp99ptcb9werdfmb" FOREIGN KEY ("chat_id") REFERENCES "public"."chats"("id");



ALTER TABLE ONLY "public"."chat_participants"
    ADD CONSTRAINT "fkn4feij8janlba38q59kl2ebgg" FOREIGN KEY ("chat_id") REFERENCES "public"."chats"("id");





ALTER PUBLICATION "supabase_realtime" OWNER TO "postgres";


GRANT USAGE ON SCHEMA "public" TO "postgres";
GRANT USAGE ON SCHEMA "public" TO "anon";
GRANT USAGE ON SCHEMA "public" TO "authenticated";
GRANT USAGE ON SCHEMA "public" TO "service_role";








































































































































































GRANT ALL ON TABLE "public"."chat_participants" TO "anon";
GRANT ALL ON TABLE "public"."chat_participants" TO "authenticated";
GRANT ALL ON TABLE "public"."chat_participants" TO "service_role";



GRANT ALL ON TABLE "public"."chats" TO "anon";
GRANT ALL ON TABLE "public"."chats" TO "authenticated";
GRANT ALL ON TABLE "public"."chats" TO "service_role";



GRANT ALL ON TABLE "public"."messages" TO "anon";
GRANT ALL ON TABLE "public"."messages" TO "authenticated";
GRANT ALL ON TABLE "public"."messages" TO "service_role";









ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES TO "postgres";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES TO "anon";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES TO "authenticated";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON SEQUENCES TO "service_role";






ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS TO "postgres";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS TO "anon";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS TO "authenticated";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON FUNCTIONS TO "service_role";






ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES TO "postgres";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES TO "anon";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES TO "authenticated";
ALTER DEFAULT PRIVILEGES FOR ROLE "postgres" IN SCHEMA "public" GRANT ALL ON TABLES TO "service_role";






























\unrestrict nNT6L1tSq76Hwstl4CkBXKqVedxIgtaaAItHMBMProetqdQQGUTfeYzODYxttOL

RESET ALL;
