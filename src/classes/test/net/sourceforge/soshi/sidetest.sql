create table sidetest_tmp
(
    col_a       varchar(10) not null,
    col_b       varchar(10) not null,
    col_c       varchar(10) not null
)
/

insert into
    sidetest_tmp
    (col_a, col_b, col_c) values ('AAA', 'BBB', 'CCC')
/

insert into
    sidetest_tmp
    (col_a, col_b, col_c) values ('aaa', 'bbb', 'ccc')
/

select
    col_a as "col_a",
    col_b as "col_b",
    col_c as "col_c"
from
    sidetest_tmp
/

drop table sidetest_tmp
/

